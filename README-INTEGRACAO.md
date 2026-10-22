# MACHRY — Front-end integrado à API

## Recursos integrados

- Interface original do MACHRY mantida.
- Opção **Cadastros API** adicionada ao menu lateral.
- Operações de cadastro, listagem, consulta por ID, atualização e exclusão para:
  - Itens (`/items`)
  - Lotes (`/lotes`)
  - Fichas técnicas (`/fichas-tecnicas`)
  - Processamentos (`/processamentos`)
- Mensagens de sucesso e erro e exemplos de JSON.

## Requisitos

- Java compatível com a versão definida no `pom.xml` (Java 25).
- Docker Desktop com Docker Compose.
- Python, caso seja necessário iniciar um servidor local para o front-end.

## Executar o projeto

### 1. Iniciar o banco de dados

Abra um terminal na pasta `Backend` e execute:

```bash
docker compose up -d
```

O PostgreSQL será iniciado na porta `5432`.

### 2. Iniciar a API

Ainda na pasta `Backend`, execute no Windows:

```bat
mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

### 3. Iniciar o front-end

Abra `Frontend/Machry/index.html` no navegador. Se as chamadas à API não funcionarem ao abrir o arquivo diretamente, inicie um servidor local dentro da pasta `Frontend/Machry`:

```bash
python -m http.server 5500
```

Acesse `http://localhost:5500`.

## Utilizar os CRUDs

1. Abra **Cadastros API** no menu lateral.
2. Escolha Itens, Lotes, Fichas técnicas ou Processamentos.
3. Clique em **Listar registros** para consultar os dados.
4. Preencha os campos ou o JSON de acordo com o recurso e clique em **Cadastrar**.
5. Para consultar, atualizar ou excluir, informe o ID e use a ação correspondente.

Os registros relacionados usados em fichas técnicas e processamentos precisam existir no banco de dados.
