# GAP / Machry — Backend Flask

O backend principal do projeto foi migrado de Java/Spring Boot para **Python/Flask**.

A migração preserva:

- modelo de domínio;
- PostgreSQL via Docker;
- endpoints REST;
- regras de negócio;
- ficha técnica (planejado);
- processamento (realizado);
- snapshot histórico das etapas;
- respostas `400`, `404` e `409`;
- Swagger/OpenAPI;
- testes automatizados.

## Estrutura

```text
GAP/
├── Backend/
│   ├── app/
│   │   ├── models/
│   │   ├── schemas/
│   │   ├── services/
│   │   ├── routes/
│   │   └── exceptions/
│   ├── tests/
│   ├── docs/
│   ├── docker-compose.yml
│   ├── requirements.txt
│   └── run.py
├── diagrama_arquitetura.png
└── fluxograma.png
```

Consulte [`Backend/README.md`](Backend/README.md) para instalação e execução.
