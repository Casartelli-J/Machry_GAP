# Machry API — Flask

Backend migrado de Spring Boot para **Flask**, preservando o domínio, os endpoints principais, as regras de negócio, PostgreSQL/Docker, tratamento de erros e documentação OpenAPI.

## Stack

- Python 3.11+
- Flask 3.1
- Flask-SQLAlchemy / SQLAlchemy 2
- Marshmallow
- Flask-Smorest (OpenAPI + Swagger UI)
- PostgreSQL 17
- Docker Compose
- pytest

## Executar o banco

```bash
docker compose up -d
```

Banco local:

```text
host: localhost
port: 5432
database: machry_database
user: masterchief
password: 1234
```

## Criar ambiente Python

Windows PowerShell:

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
Copy-Item .env.example .env
python run.py
```

Linux/macOS:

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
python run.py
```

API: `http://localhost:5000`

## Swagger e OpenAPI

- Swagger UI: `http://localhost:5000/swagger-ui`
- OpenAPI JSON: `http://localhost:5000/openapi.json`

## Arquitetura

```text
HTTP / JSON
   ↓
routes/        Blueprint + Flask-Smorest
   ↓
schemas/       Marshmallow: entrada, saída e validação
   ↓
services/      regras de negócio e transações
   ↓
models/        SQLAlchemy
   ↓
PostgreSQL
```

Não foi criada uma camada `repository/` apenas para imitar o Spring Data JPA. O SQLAlchemy já fornece a abstração de persistência necessária; os `services` continuam responsáveis pelas regras do domínio.

## Domínio preservado

```text
Item
├── Lote
└── FichaTecnica
    ├── IngredienteFicha
    └── EtapaFichaTecnica

Processamento
├── ItemProcessamento
└── EtapaProcessamento
```

### Planejado x realizado

- `FichaTecnica`: ingredientes, quantidades e etapas **previstas**.
- `Processamento`: quantidades e tempos **realmente utilizados/executados**.

`EtapaProcessamento` mantém um snapshot da etapa planejada. O ID de origem é armazenado sem FK propositalmente, para que o histórico da produção não seja alterado se a ficha técnica mudar depois.

## Regras mantidas

- INSUMO não pode possuir ficha técnica.
- INSUMO não pode ser produto de processamento.
- produto não pode ser ingrediente da própria ficha.
- um produto possui no máximo uma ficha técnica.
- etapas de uma ficha não podem repetir a ordem.
- ficha do processamento deve pertencer ao produto informado.
- etapa realizada deve pertencer à ficha escolhida.
- uma mesma etapa não pode ser registrada duas vezes no processamento.
- data de validade do lote não pode ser anterior à produção.
- quantidades e tempos precisam ser positivos.

## Estoque: o que continua fora deste módulo

O processamento **registra consumo real**, mas não executa movimentação de estoque.

Continuam fora deste backend, aguardando integração com o módulo/time responsável por estoque:

- baixa automática de saldo;
- movimentação de entrada/saída;
- FIFO por validade;
- seleção/consumo automático de lote;
- localização física do estoque;
- transferências;
- saldo consolidado por lote/localização;
- regras de separação da produção.

Não foi implementada uma `MovimentacaoEstoque` nesta migração para evitar duplicar responsabilidade de outro módulo.

## Erros padronizados

Exemplo:

```json
{
  "timestamp": "2026-10-30T12:00:00+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "O recurso não pode ser alterado ou excluído porque está sendo utilizado por outros registros.",
  "path": "/items/1",
  "fields": {}
}
```

## Testes

Os testes usam um banco PostgreSQL separado do banco de desenvolvimento:

```text
machry_database -> desenvolvimento
machry_test     -> pytest
```

A configuração vem do `.env`:

```env
DATABASE_URL=postgresql+psycopg://masterchief:1234@localhost:5432/machry_database
TEST_DATABASE_URL=postgresql+psycopg://masterchief:1234@localhost:5432/machry_test
```

O `pytest` cria `machry_test` automaticamente caso ele ainda não exista. O `conftest.py` também bloqueia a execução se `TEST_DATABASE_URL` apontar para o mesmo banco de `DATABASE_URL`, evitando apagar tabelas do banco de desenvolvimento.

Execute a partir da pasta `Backend`:

```bash
python -m pytest -q
```

Os testes cobrem CRUD/status HTTP, validação, regra de ficha técnica, processamento e conflito de exclusão. A exclusão de um `Item` referenciado por lote, ficha técnica ou processamento retorna `409 Conflict`.

## Observação sobre schema/migrations

Durante desenvolvimento, `AUTO_CREATE_DB=true` executa `db.create_all()` para criar tabelas ausentes, comportamento simples equivalente ao uso de criação automática durante a fase inicial do Spring.

`create_all()` **não substitui migrations**. Quando o schema estabilizar, o próximo passo recomendado é adicionar Alembic/Flask-Migrate antes de produção.
