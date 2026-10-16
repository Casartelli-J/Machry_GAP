# Machry API

Backend do projeto **Machry**, desenvolvido com Java e Spring Boot para gerenciar itens, lotes, fichas técnicas e processamentos de produção.

O foco atual da API é representar o fluxo de **cadastro → planejamento da produção → execução real**, mantendo separado o domínio de movimentação de estoque, que pertence a outro módulo/time e deverá ser integrado posteriormente.

---

## Sumário

- [Visão geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Como executar o PostgreSQL com Docker](#como-executar-o-postgresql-com-docker)
- [Como executar a API](#como-executar-a-api)
- [Swagger e OpenAPI](#swagger-e-openapi)
- [Arquitetura do projeto](#arquitetura-do-projeto)
- [Modelo de domínio](#modelo-de-domínio)
- [Endpoints principais](#endpoints-principais)
- [Evolução do projeto](#evolução-do-projeto)
- [Decisões de arquitetura e modelagem](#decisões-de-arquitetura-e-modelagem)
- [Contribuição externa da Ficha Técnica](#contribuição-externa-da-ficha-técnica)
- [O que foi mantido e o que foi descartado](#o-que-foi-mantido-e-o-que-foi-descartado)
- [Limites do módulo de estoque](#limites-do-módulo-de-estoque)
- [Validação e tratamento de erros](#validação-e-tratamento-de-erros)
- [Testes](#testes)
- [Banco de dados e schema](#banco-de-dados-e-schema)
- [Próximos passos](#próximos-passos)

---

# Visão geral

A API foi construída para representar quatro áreas principais:

1. **Item** — cadastro base de insumos, produtos intermediários e produtos finais.
2. **Lote** — registro de lotes vinculados a itens.
3. **Ficha Técnica** — definição do que deve ser utilizado e de como um produto deve ser produzido.
4. **Processamento** — registro do que realmente foi utilizado e executado durante a produção.

O fluxo principal pode ser resumido assim:

```text
Item
 │
 ├── Lote
 │
 └── FichaTecnica
      ├── IngredienteFicha
      └── EtapaFichaTecnica
               │
               ▼
          Processamento
          ├── ItemProcessamento
          └── EtapaProcessamento
```

Conceitualmente:

```text
PLANEJADO

Ficha Técnica
├── ingredientes previstos
└── etapas e tempos previstos

        ↓

REALIZADO

Processamento
├── quantidades realmente utilizadas
└── etapas e tempos realmente realizados
```

Exemplo:

```text
Previsto: 500 g de farinha
Real:     523 g de farinha

Previsto: 40 minutos de cocção
Real:     43 minutos de cocção
```

---

# Tecnologias

O projeto atualmente utiliza:

- **Java 25**
- **Spring Boot 4.1.1**
- **Spring Web MVC**
- **Spring Data JPA**
- **Hibernate**
- **Jakarta Bean Validation**
- **PostgreSQL 17**
- **Docker / Docker Compose**
- **Springdoc OpenAPI 3.1.1**
- **Swagger UI**
- **JUnit 5**
- **Mockito**
- **MockMvc**
- **H2** para testes automatizados
- **Maven Wrapper**

---

# Pré-requisitos

Para executar o projeto localmente, tenha instalado:

- Java **25**
- Docker Desktop ou Docker Engine com Docker Compose
- Git, caso o projeto seja clonado de um repositório

Não é necessário instalar Maven globalmente, pois o projeto possui Maven Wrapper:

```text
mvnw
mvnw.cmd
```

Para conferir sua versão do Java:

```bash
java -version
```

---

# Como executar o PostgreSQL com Docker

O projeto possui um `docker-compose.yml` na raiz com um PostgreSQL já configurado.

Configuração atual:

```yaml
POSTGRES_DB: machry_database
POSTGRES_USER: masterchief
POSTGRES_PASSWORD: 1234
```

Porta exposta:

```text
5432
```

A aplicação se conecta utilizando:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/machry_database
spring.datasource.username=masterchief
spring.datasource.password=1234
```

> As credenciais atuais são de desenvolvimento. Em produção, não devem ficar fixas no repositório; devem ser fornecidas por variáveis de ambiente ou secret manager.

## 1. Iniciar o banco

Na pasta raiz do backend:

```bash
docker compose up -d
```

O Docker criará o container:

```text
postgres_db
```

## 2. Conferir se está rodando

```bash
docker compose ps
```

ou:

```bash
docker ps
```

## 3. Ver os logs do PostgreSQL

```bash
docker compose logs -f postgres
```

Para sair dos logs sem desligar o container:

```text
Ctrl + C
```

## 4. Parar o banco

```bash
docker compose down
```

Isso remove o container e a rede do Compose, mas mantém os dados porque existe um volume persistente:

```yaml
postgres_data:/var/lib/postgresql/data
```

## 5. Apagar também os dados do banco

Use somente quando realmente quiser reiniciar o ambiente do zero:

```bash
docker compose down -v
```

> `-v` remove o volume e apaga os dados persistidos.

## Porta 5432 ocupada

Se já existir outro PostgreSQL usando a porta `5432`, o Docker poderá falhar ao iniciar.

Nesse caso, é possível alterar o mapeamento no `docker-compose.yml`, por exemplo:

```yaml
ports:
  - "5433:5432"
```

E atualizar a URL da aplicação:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/machry_database
```

---

# Como executar a API

Primeiro, deixe o PostgreSQL rodando.

Depois, na raiz do projeto:

## Windows

```powershell
.\mvnw.cmd spring-boot:run
```

## Linux / macOS

```bash
./mvnw spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

Para gerar o build:

### Windows

```powershell
.\mvnw.cmd clean package
```

### Linux / macOS

```bash
./mvnw clean package
```

---

# Swagger e OpenAPI

A documentação automática foi adicionada na etapa de estabilização da API utilizando `springdoc-openapi`.

## Swagger UI

Com a aplicação rodando, abra:

```text
http://localhost:8080/swagger-ui.html
```

O Swagger permite:

- visualizar os endpoints disponíveis;
- consultar os schemas dos DTOs;
- conferir os métodos HTTP;
- visualizar os corpos de request;
- executar requisições diretamente pelo navegador;
- consultar a documentação das operações.

## Documento OpenAPI em JSON

```text
http://localhost:8080/v3/api-docs
```

Esse endpoint é útil para:

- gerar clientes automaticamente;
- importar o contrato em outras ferramentas;
- integração com front-end;
- documentação externa;
- ferramentas que entendem a especificação OpenAPI.

A configuração principal está em:

```text
src/main/java/com/machry/machryapi/config/OpenApiConfig.java
```

Os controllers também possuem anotações como:

```java
@Tag
@Operation
@ApiResponse
```

Além do Swagger, existe documentação textual em:

```text
docs/API.md
```

---

# Arquitetura do projeto

Foi adotada uma arquitetura em camadas simples:

```text
controller
    ↓
service
    ↓
repository
    ↓
model
```

Com DTOs entre a API HTTP e o domínio:

```text
Request JSON
    ↓
RequestDTO
    ↓
Controller
    ↓
Service
    ↓
Entity
    ↓
Repository
```

Na resposta:

```text
Entity
  ↓
ResponseDTO
  ↓
JSON
```

Estrutura principal:

```text
src/main/java/com/machry/machryapi/
├── config/
├── controller/
├── dto/
│   ├── error/
│   ├── etapa/
│   ├── fichatecnica/
│   ├── item/
│   ├── lote/
│   └── processamento/
├── exception/
├── model/
│   └── enums/
├── repository/
├── service/
└── MachryApiApplication.java
```

## Por que manter Controller, Service e Repository separados?

- **Controller** lida com HTTP.
- **Service** concentra regras de negócio e transações.
- **Repository** lida com persistência.
- **Model** representa o domínio.
- **DTO** define o contrato da API e evita expor diretamente as entidades JPA.

Não foram criadas interfaces `Service` + `ServiceImpl`, pois isso adicionaria boilerplate sem necessidade no estado atual do projeto.

---

# Modelo de domínio

## Item

Representa tudo que pode participar do sistema produtivo.

Tipos disponíveis:

```java
INSUMO
INTERMEDIARIO
FINAL
```

Exemplos:

```text
Farinha       → INSUMO
Massa de bolo → INTERMEDIARIO
Bolo pronto   → FINAL
```

Campos principais:

```text
id
nome
tipo
unidadeMedida
```

`TipoItem` foi implementado como `enum` para evitar valores arbitrários de texto.

---

## Lote

Um lote pertence a um Item:

```text
Item 1 ───── N Lote
```

Campos:

```text
id
item
quantidade
dataProducao
dataEntrada
dataValidade
```

Regra implementada:

```text
dataValidade >= dataProducao
```

O relacionamento é mantido de forma simples, com o lote apontando para o item.

Não foi adicionada uma coleção de lotes dentro de `Item`, pois isso não era necessário para os casos de uso atuais e evitaria complexidade desnecessária.

---

## Ficha Técnica

A Ficha Técnica representa o planejamento de produção de um produto.

```text
Item produto
     │
     ▼
FichaTecnica
├── rendimento
├── unidadeRendimento
├── IngredienteFicha[]
└── EtapaFichaTecnica[]
```

Um produto pode ter apenas uma ficha técnica:

```java
@OneToOne
```

Isso é reforçado tanto pela relação JPA quanto por validação no service.

Um `INSUMO` não pode possuir ficha técnica.

A ficha é destinada a produtos:

```text
INTERMEDIARIO
FINAL
```

---

## IngredienteFicha

Representa um ingrediente planejado da receita.

```text
FichaTecnica
     │
     └── IngredienteFicha
             │
             ├── Item
             └── quantidade
```

Não armazenamos o nome do ingrediente como `String`.

O ingrediente referencia o próprio `Item`, evitando duplicação de dados e inconsistências.

Também não duplicamos a unidade de medida dentro da entidade de ingrediente. A unidade vem de:

```text
Item.unidadeMedida
```

A quantidade usa `BigDecimal`.

---

## EtapaFichaTecnica

Representa uma etapa planejada da produção.

Campos:

```text
ordem
nome
tipoMetodo
descricao
tempoPrevistoMinutos
```

Tipos de método atuais:

```java
PREPARO
COCCAO
MONTAGEM
RESFRIAMENTO
```

Cada etapa possui seu próprio tipo de método.

Isso foi escolhido porque uma ficha pode possuir várias etapas diferentes. Colocar apenas um `TipoMetodo` diretamente na Ficha Técnica limitaria incorretamente todo o processo a um único método.

Não podem existir duas etapas com a mesma ordem dentro da mesma ficha.

---

## Processamento

Representa uma execução real da produção.

```text
Processamento
├── produto
├── fichaTecnica utilizada
├── quantidadeProduzida
├── dataProcessamento
├── ItemProcessamento[]
└── EtapaProcessamento[]
```

Ele não representa apenas a receita prevista: ele registra aquilo que realmente aconteceu.

Exemplo:

```text
Ficha Técnica: 500 g
Processamento:  523 g
```

---

## ItemProcessamento

Representa a quantidade real de um Item utilizada no processamento.

```text
Processamento
     │
     └── ItemProcessamento
             │
             ├── Item
             └── quantidadeUtilizada
```

Novamente, o item é referenciado por relacionamento e a quantidade utiliza `BigDecimal`.

---

## EtapaProcessamento

Representa uma etapa realmente executada.

Ela armazena:

```text
etapaFichaTecnicaIdOrigem
ordem
nome
tipoMetodo
tempoPrevistoMinutos
tempoRealizadoMinutos
observacao
```

### Por que a EtapaProcessamento guarda um snapshot?

A etapa realizada não mantém uma FK obrigatória para `EtapaFichaTecnica`.

Ao criar o processamento, são copiados dados da etapa planejada:

```text
ID de origem
ordem
nome
tipo
tempo previsto
```

O objetivo é preservar histórico.

Exemplo:

```text
Hoje:
Ficha Técnica → Assar por 40 minutos
Processamento → realizado em 43 minutos
```

Se no futuro a ficha for alterada para 35 minutos, o processamento antigo continua registrando:

```text
previsto na época: 40
realizado:         43
```

Assim, alterações futuras da ficha não reescrevem semanticamente o histórico de produções passadas.

---

# Endpoints principais

Todos os recursos principais seguem CRUD REST.

## Items

```http
POST   /items
GET    /items
GET    /items/{id}
PUT    /items/{id}
DELETE /items/{id}
```

## Lotes

```http
POST   /lotes
GET    /lotes
GET    /lotes/{id}
PUT    /lotes/{id}
DELETE /lotes/{id}
```

## Fichas Técnicas

```http
POST   /fichas-tecnicas
GET    /fichas-tecnicas
GET    /fichas-tecnicas/{id}
PUT    /fichas-tecnicas/{id}
DELETE /fichas-tecnicas/{id}
```

## Processamentos

```http
POST   /processamentos
GET    /processamentos
GET    /processamentos/{id}
PUT    /processamentos/{id}
DELETE /processamentos/{id}
```

`IngredienteFicha`, `ItemProcessamento`, `EtapaFichaTecnica` e `EtapaProcessamento` não possuem controllers independentes.

Eles são tratados como partes dos agregados:

```text
FichaTecnica
├── IngredienteFicha
└── EtapaFichaTecnica

Processamento
├── ItemProcessamento
└── EtapaProcessamento
```

Essa decisão reduz endpoints artificiais e mantém as alterações desses objetos dentro do contexto ao qual pertencem.

---

# Evolução do projeto

## Base do projeto

O projeto foi organizado com:

- Spring Boot;
- PostgreSQL;
- JPA/Hibernate;
- validação;
- Docker Compose;
- Maven Wrapper;
- estrutura em camadas.

## CRUD de Item

Foi implementado o primeiro CRUD completo:

- criação;
- listagem;
- busca por ID;
- atualização;
- exclusão;
- enum `TipoItem`;
- DTO de request e response;
- exceção específica para Item inexistente.

## Fase 5 — Lote

Foi introduzido o primeiro relacionamento entre entidades:

```text
Lote → Item
```

Foram adicionados:

- `Lote`;
- DTOs;
- repository;
- service;
- controller;
- exceção específica;
- validação das datas.

## Fase 6 — Ficha Técnica

Foi criada a estrutura de receita planejada:

```text
FichaTecnica
└── IngredienteFicha[]
```

Também foram incorporados conceitos úteis enviados por outro integrante:

- rendimento;
- unidade de rendimento;
- composição da ficha;
- persistência em cascata;
- `orphanRemoval`.

A implementação original recebida foi adaptada à arquitetura do projeto.

## Fase 7 — Processamento

Foi criado o registro da produção real:

```text
Processamento
└── ItemProcessamento[]
```

A partir daqui tornou-se possível comparar:

```text
quantidade planejada × quantidade utilizada
```

## Fase 8 — Etapas de Produção

Foram adicionadas etapas planejadas e realizadas:

```text
FichaTecnica
└── EtapaFichaTecnica[]

Processamento
└── EtapaProcessamento[]
```

O enum `TipoMetodo`, que anteriormente não tinha um lugar adequado, passou a fazer sentido em cada etapa individualmente.

Também foi implementado o snapshot histórico das etapas de processamento.

## Fase 9 — Robustez, testes e documentação

Foram adicionados:

- `BusinessRuleException`;
- `ApiErrorResponse`;
- `GlobalExceptionHandler` mais completo;
- resposta `409 Conflict` para violações de integridade;
- Swagger/OpenAPI;
- documentação textual da API;
- testes JUnit/Mockito;
- testes de controllers com MockMvc;
- H2 em modo compatível com PostgreSQL para testes;
- validação dos status HTTP;
- correção explícita dos `@PathVariable("id")`.

Também foram realizados testes de fluxo completo e cenários de erro no Postman.

---

# Decisões de arquitetura e modelagem

## DTOs em vez de expor entidades

Os controllers não recebem nem retornam diretamente entidades JPA.

Motivos:

- separar contrato HTTP do modelo persistente;
- evitar serialização acidental de relações JPA;
- evitar ciclos JSON;
- permitir evoluir o banco sem necessariamente quebrar a API;
- aplicar validações específicas de entrada;
- controlar exatamente o que é retornado.

---

## DTOs implementados como `record`

DTOs são objetos imutáveis de transporte e não precisam de comportamento complexo.

Por isso foram usados Java `record` para reduzir código repetitivo.

---

## `BigDecimal` em quantidades

Quantidades e rendimentos utilizam `BigDecimal` em vez de `Double`.

Isso evita a utilização de ponto flutuante binário para valores de domínio em que precisão é importante.

---

## Enums persistidos como texto

Enums usam:

```java
@Enumerated(EnumType.STRING)
```

Isso deixa o banco legível e evita que a semântica dependa da posição numérica do enum.

Exemplo:

```text
INSUMO
INTERMEDIARIO
FINAL
```

em vez de:

```text
0
1
2
```

---

## `@Transactional` nos services

Métodos de escrita são transacionais para que uma operação de negócio seja confirmada ou revertida como uma unidade.

Métodos de leitura utilizam:

```java
@Transactional(readOnly = true)
```

Isso também mantém o contexto JPA adequado durante a conversão das entidades relacionadas para DTO.

---

## `cascade = CascadeType.ALL` e `orphanRemoval = true`

Foi utilizado em relações pertencentes ao agregado, como:

```text
FichaTecnica → IngredienteFicha
FichaTecnica → EtapaFichaTecnica
Processamento → ItemProcessamento
Processamento → EtapaProcessamento
```

Isso significa que os filhos são gerenciados pela entidade principal.

Ao substituir uma lista de ingredientes ou etapas, os registros antigos removidos da coleção podem ser eliminados automaticamente.

---

## `FetchType.LAZY`

Relacionamentos `ManyToOne` e `OneToOne` relevantes usam carregamento lazy para evitar carregar automaticamente árvores de objetos que não são sempre necessárias.

---

## Regras simples no DTO e regras de negócio no Service

Exemplo de validação de campo:

```java
@NotNull
@Positive
```

fica no DTO.

Exemplo de regra de negócio:

```text
data de validade não pode ser anterior à produção
```

fica no service.

Outro exemplo:

```text
INSUMO não pode possuir Ficha Técnica
```

fica no service.

Essa separação evita misturar validação estrutural da requisição com lógica de domínio.

---

## `BusinessRuleException`

Inicialmente regras de negócio utilizavam `IllegalArgumentException`.

Foi criada uma exceção específica:

```java
BusinessRuleException
```

Ela torna a intenção mais clara e permite um tratamento HTTP centralizado como `400 Bad Request`.

---

## `409 Conflict` para integridade referencial

Durante os testes foi identificado um caso real:

```http
DELETE /items/{id}
```

podia resultar em `500 Internal Server Error` quando o item estivesse referenciado por outro registro.

A API passou a tratar `DataIntegrityViolationException` e retornar:

```text
409 Conflict
```

Isso representa melhor a situação: o request é válido, porém conflita com o estado atual dos dados.

---

## `@PathVariable("id")` explícito

Foi encontrado em teste um erro de resolução do nome do parâmetro em endpoints como:

```http
GET /processamentos/{id}
```

Os controllers passaram a utilizar explicitamente:

```java
@PathVariable("id") Long id
```

Também foi configurado no compilador Maven:

```xml
<parameters>true</parameters>
```

O resultado é um contrato mais explícito e menos dependente de reflexão sobre nomes de parâmetros.

---

# Contribuição externa da Ficha Técnica

Uma implementação de Ficha Técnica enviada por outro integrante foi avaliada e parcialmente incorporada.

Ela trouxe conceitos úteis, mas havia diferenças em relação ao modelo e à arquitetura já adotados.

A estratégia foi **aproveitar a ideia de domínio**, e não copiar diretamente a implementação.

---

# O que foi mantido e o que foi descartado

## Mantido da contribuição

### Estrutura Ficha Técnica + composição

A ideia de uma ficha possuir uma coleção de componentes foi mantida.

Ela evoluiu para:

```text
FichaTecnica
└── IngredienteFicha[]
```

### `cascade = CascadeType.ALL`

Mantido para que a Ficha Técnica persista seus componentes junto com ela.

### `orphanRemoval = true`

Mantido para remover componentes que deixarem de pertencer à ficha durante uma atualização.

### Rendimento

Mantido:

```text
rendimento
unidadeRendimento
```

Esse dado representa a saída planejada da receita.

### `TipoMetodo`

A ideia foi mantida, porém reposicionada.

Em vez de existir diretamente na Ficha Técnica, passou a pertencer a cada `EtapaFichaTecnica`.

Isso permite:

```text
PREPARO
↓
COCCAO
↓
MONTAGEM
↓
RESFRIAMENTO
```

na mesma ficha.

---

## Descartado ou alterado da contribuição

### Ingrediente como `String`

Descartado:

```java
String ingrediente
```

Substituído por relacionamento:

```text
IngredienteFicha → Item
```

Motivo: o Item já é o cadastro central do domínio.

### Unidade de medida duplicada na composição

Descartada.

A unidade passa a vir de:

```text
Item.unidadeMedida
```

### `Double` para quantidades

Substituído por:

```java
BigDecimal
```

### Entidades diretamente no Controller

Descartado.

A API mantém RequestDTO e ResponseDTO.

### `RuntimeException` genérica

Substituída por exceções de domínio/aplicação específicas.

### Nome duplicado na Ficha Técnica

A ficha não precisa duplicar o nome do produto.

Ela referencia:

```text
FichaTecnica → Item produto
```

O nome é obtido do próprio Item.

### Um único método para a Ficha Técnica

Descartado porque uma receita pode possuir várias fases.

O tipo de método passou para cada etapa.

---

# Limites do módulo de estoque

Esta é uma das decisões mais importantes do projeto atual.

Existe outro time/módulo responsável pela **movimentação de estoque**.

Por esse motivo, este backend deliberadamente **não implementa** a lógica de estoque operacional.

## Não implementado neste módulo

```text
FIFO
baixa automática de estoque
entrada de estoque
saída de estoque
movimentação de estoque
transformação de estoque
escolha automática de lote
separação automática por validade
localização física de estoque
saldo de estoque
reserva de estoque
lista de separação
```

## O que o nosso módulo faz

Ele registra informações necessárias para futura integração.

Exemplo:

```text
Processamento
→ Item Farinha
→ quantidade utilizada: 523 g
```

Isso significa:

> Foram utilizados 523 g de Farinha no processamento.

Mas o `ProcessamentoService` **não decide de qual lote tirar**, **não reduz saldo** e **não cria uma movimentação de estoque**.

Essa responsabilidade deve ser resolvida pelo módulo de estoque quando existir um contrato de integração entre os times.

## Por que não implementar agora?

Implementar essa lógica sem o contrato do outro módulo criaria:

- duplicação de responsabilidade;
- regras concorrentes;
- risco de duas partes do sistema alterarem o mesmo saldo;
- acoplamento incorreto;
- retrabalho quando a API de estoque fosse definida.

Portanto, a decisão atual é:

```text
Machry API de produção
→ registra o consumo real

Módulo de estoque
→ executa a movimentação física/lógica do estoque
```

## Lote existe, mas não representa um motor de estoque

A entidade `Lote` foi mantida porque é parte do domínio e será necessária para integração futura.

Entretanto, a existência de `Lote` não significa que o backend atual implemente FIFO ou saldo.

---

# Validação e tratamento de erros

A API utiliza um handler global:

```text
GlobalExceptionHandler
```

Os erros seguem o formato:

```json
{
  "timestamp": "2026-10-16T06:20:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Mensagem do erro",
  "path": "/endpoint",
  "fields": {}
}
```

## Status utilizados

| Situação | Status |
|---|---:|
| POST válido | `201 Created` |
| GET válido | `200 OK` |
| PUT válido | `200 OK` |
| DELETE válido | `204 No Content` |
| Validação inválida | `400 Bad Request` |
| Regra de negócio inválida | `400 Bad Request` |
| JSON malformado | `400 Bad Request` |
| Recurso inexistente | `404 Not Found` |
| Conflito de integridade | `409 Conflict` |

## Exemplos de regras implementadas

### Lote

```text
dataValidade não pode ser anterior a dataProducao
```

### Ficha Técnica

```text
INSUMO não pode possuir ficha
produto não pode ser ingrediente dele mesmo
produto só pode possuir uma ficha
ordens de etapas não podem se repetir
```

### Processamento

```text
INSUMO não pode ser produto de processamento
ficha precisa pertencer ao produto informado
etapa precisa pertencer à ficha informada
mesma etapa não pode ser registrada duas vezes
```

---

# Testes

O projeto possui testes automatizados em Java e também foi validado manualmente com Postman.

## Executar todos os testes

### Windows

```powershell
.\mvnw.cmd clean test
```

### Linux / macOS

```bash
./mvnw clean test
```

## Testes de Service

Existem testes para:

```text
ItemServiceTest
LoteServiceTest
FichaTecnicaServiceTest
ProcessamentoServiceTest
```

Eles utilizam JUnit e Mockito para validar regras de serviço sem depender do banco PostgreSQL real.

## Testes de Controller

`ControllerHttpStatusTest` utiliza MockMvc para validar o contrato HTTP.

São verificados, entre outros:

```text
POST   → 201
GET    → 200
PUT    → 200
DELETE → 204
```

Também existe teste para:

```text
DELETE bloqueado por dependência → 409
```

## Banco de teste

Os testes utilizam H2 em memória:

```properties
jdbc:h2:mem:machry_test;MODE=PostgreSQL
```

Isso evita obrigar o desenvolvedor a iniciar o PostgreSQL para executar a suíte de testes.

## Testes com Postman

Durante o desenvolvimento foi utilizado um fluxo completo no Postman para validar:

```text
criação de insumos
criação de produto intermediário
criação de ficha técnica
criação de produto final
criação de processamento
consulta do processamento
cenários inválidos
```

Também foi criado um script automatizado com `pm.sendRequest(...)` para validar cenários como:

```text
404 para processamento inexistente
400 para ficha de INSUMO
400 para produto como próprio ingrediente
400 para ordem de etapa duplicada
400 para ficha de outro produto
400 para etapa de outra ficha
400 para etapa repetida
400 para Bean Validation
400 para quantidades negativas
```

---

# Banco de dados e schema

Atualmente o desenvolvimento utiliza:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Portanto, Hibernate/JPA é responsável por criar e evoluir as tabelas necessárias de acordo com as entidades atuais.

## Sobre `machry_db.sql`

O repositório ainda contém:

```text
machry_db.sql
```

Esse arquivo representa uma versão anterior do modelo e **não deve ser considerado a fonte de verdade do schema atual**.

O modelo Java evoluiu depois dele, adicionando por exemplo:

```text
EtapaFichaTecnica
EtapaProcessamento
novos relacionamentos da FichaTecnica
novos relacionamentos do Processamento
BigDecimal
nomes de tabelas/colunas atuais do JPA
```

Durante a fase atual, a decisão foi focar no domínio Spring/JPA e deixar a definição final de banco/migrations para alinhamento posterior com infraestrutura.

Antes de produção, o recomendado é substituir `ddl-auto=update` por migrations versionadas, por exemplo com Flyway ou Liquibase.

---

# Próximos passos

O núcleo atual está preparado para a próxima fase de integração.

Principais evoluções esperadas:

1. definir o contrato de integração com o módulo de estoque;
2. conectar consumo de `ItemProcessamento` à movimentação de estoque;
3. definir como lotes serão selecionados pelo estoque;
4. decidir como tratar FIFO, validade e localização no módulo responsável;
5. adicionar autenticação/autorização quando os requisitos estiverem definidos;
6. mover credenciais para variáveis de ambiente;
7. adotar migrations de banco;
8. ampliar cobertura de testes de integração;
9. revisar paginação/filtros para listagens quando o volume de dados crescer;
10. preparar configuração específica de produção.

---

# Resumo das principais decisões

| Tema | Decisão |
|---|---|
| Arquitetura | Controller → Service → Repository |
| Contrato HTTP | DTOs separados das entidades |
| DTOs | Java `record` |
| Quantidades | `BigDecimal` |
| Tipos fechados | `enum` persistido como `STRING` |
| Ficha ↔ Produto | `OneToOne` |
| Ingredientes da ficha | relacionados a `Item`, não `String` |
| Filhos da ficha/processamento | `cascade ALL` + `orphanRemoval` |
| Métodos de produção | associados às etapas, não à ficha inteira |
| Histórico de etapas realizadas | snapshot no Processamento |
| Regra de domínio | Service + `BusinessRuleException` |
| Validação de campos | Bean Validation nos DTOs |
| Integridade referencial | `409 Conflict` |
| Documentação | Swagger/OpenAPI + `docs/API.md` |
| Banco local | PostgreSQL via Docker |
| Banco de testes | H2 em memória |
| Schema em desenvolvimento | Hibernate `ddl-auto=update` |
| Movimentação de estoque | fora deste módulo por enquanto |

---

## Estado atual

O backend já consegue representar o fluxo de produção do ponto de vista do planejamento e da execução:

```text
CADASTRO
   ↓
ITEM
   ↓
FICHA TÉCNICA
   ├── ingredientes previstos
   └── etapas previstas
   ↓
PROCESSAMENTO
   ├── itens realmente utilizados
   └── etapas realmente realizadas
```

O próximo grande marco arquitetural não é adicionar mais CRUDs isolados, mas integrar esse núcleo com os demais módulos do sistema, principalmente o estoque.
