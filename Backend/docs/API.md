# Machry API — guia rápido

Base local padrão:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Contrato de status HTTP

| Operação | Status esperado |
|---|---:|
| POST válido | 201 Created |
| GET válido | 200 OK |
| PUT válido | 200 OK |
| DELETE válido | 204 No Content |
| Regra de negócio inválida | 400 Bad Request |
| Validação de campos inválida | 400 Bad Request |
| Recurso inexistente | 404 Not Found |
| Exclusão/alteração impedida por dependências | 409 Conflict |

## Item

### POST `/items`

```json
{
  "nome": "Farinha",
  "tipo": "INSUMO",
  "unidadeMedida": "G"
}
```

Tipos aceitos:

```text
INSUMO
INTERMEDIARIO
FINAL
```

## Lote

### POST `/lotes`

```json
{
  "itemId": 1,
  "quantidade": 25.5,
  "dataProducao": "2026-10-15",
  "dataEntrada": "2026-10-16",
  "dataValidade": "2026-11-15"
}
```

Regra principal: `dataValidade` não pode ser anterior a `dataProducao`.

## Ficha Técnica

### POST `/fichas-tecnicas`

```json
{
  "produtoId": 4,
  "rendimento": 1000,
  "unidadeRendimento": "G",
  "ingredientes": [
    {
      "itemId": 1,
      "quantidade": 500
    },
    {
      "itemId": 2,
      "quantidade": 300
    }
  ],
  "etapas": [
    {
      "ordem": 1,
      "nome": "Misturar ingredientes",
      "tipoMetodo": "PREPARO",
      "descricao": "Misturar até obter massa homogênea",
      "tempoPrevistoMinutos": 10
    },
    {
      "ordem": 2,
      "nome": "Assar",
      "tipoMetodo": "COCCAO",
      "descricao": "Levar ao forno",
      "tempoPrevistoMinutos": 40
    }
  ]
}
```

Regras principais:

- `INSUMO` não pode possuir ficha técnica.
- Um produto não pode ser ingrediente da própria ficha.
- Um produto só pode possuir uma ficha técnica.
- Não pode haver duas etapas com a mesma ordem.

## Processamento

### POST `/processamentos`

```json
{
  "produtoId": 4,
  "fichaTecnicaId": 1,
  "quantidadeProduzida": 980,
  "dataProcessamento": "2026-10-16",
  "itensUtilizados": [
    {
      "itemId": 1,
      "quantidadeUtilizada": 523
    },
    {
      "itemId": 2,
      "quantidadeUtilizada": 305
    }
  ],
  "etapasRealizadas": [
    {
      "etapaFichaTecnicaId": 1,
      "tempoRealizadoMinutos": 12,
      "observacao": "Mistura demorou um pouco mais"
    },
    {
      "etapaFichaTecnicaId": 2,
      "tempoRealizadoMinutos": 43,
      "observacao": "Forno demorou para atingir temperatura"
    }
  ]
}
```

Regras principais:

- A ficha técnica precisa pertencer ao produto informado.
- `INSUMO` não pode ser produto de processamento.
- A etapa realizada precisa pertencer à ficha utilizada.
- A mesma etapa não pode ser registrada duas vezes no processamento.

## Resposta de erro padronizada

Exemplo de `404`:

```json
{
  "timestamp": "2026-10-16T06:20:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Item não encontrado: 999",
  "path": "/items/999",
  "fields": {}
}
```

Exemplo de validação `400`:

```json
{
  "timestamp": "2026-10-16T06:20:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Falha de validação nos campos da requisição",
  "path": "/items",
  "fields": {
    "nome": "O nome é obrigatório"
  }
}
```

Exemplo de conflito `409`:

```json
{
  "timestamp": "2026-10-16T06:20:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "O recurso não pode ser alterado ou excluído porque está sendo utilizado por outros registros",
  "path": "/items/1",
  "fields": {}
}
```
