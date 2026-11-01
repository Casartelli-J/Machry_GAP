# Contratos principais da API

Base URL local: `http://localhost:5000`

## Endpoints

- `GET|POST /items`
- `GET|PUT|DELETE /items/{id}`
- `GET|POST /lotes`
- `GET|PUT|DELETE /lotes/{id}`
- `GET|POST /fichas-tecnicas`
- `GET|PUT|DELETE /fichas-tecnicas/{id}`
- `GET|POST /processamentos`
- `GET|PUT|DELETE /processamentos/{id}`
- `GET /health`

## Status esperados

- POST: `201 Created`
- GET: `200 OK`
- PUT: `200 OK`
- DELETE: `204 No Content`
- validação/regra de negócio: `400 Bad Request`
- recurso inexistente: `404 Not Found`
- integridade/recurso em uso: `409 Conflict`

## Swagger / OpenAPI

- Swagger UI: `http://localhost:5000/swagger-ui`
- JSON OpenAPI: `http://localhost:5000/openapi.json`
