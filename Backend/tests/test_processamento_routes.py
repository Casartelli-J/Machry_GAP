def create_item(client, nome, tipo, unidade="G"):
    return client.post(
        "/items", json={"nome": nome, "tipo": tipo, "unidadeMedida": unidade}
    ).get_json()["id"]


def test_processamento_registra_realizado(client):
    ingrediente = create_item(client, "Farinha", "INSUMO")
    produto = create_item(client, "Massa", "INTERMEDIARIO")
    ficha = client.post("/fichas-tecnicas", json={
        "produtoId": produto,
        "rendimento": 1000,
        "unidadeRendimento": "G",
        "ingredientes": [{"itemId": ingrediente, "quantidade": 500}],
        "etapas": [{"ordem": 1, "nome": "Misturar", "tipoMetodo": "PREPARO", "tempoPrevistoMinutos": 10}],
    }).get_json()

    etapa_id = ficha["etapas"][0]["id"]
    response = client.post("/processamentos", json={
        "produtoId": produto,
        "fichaTecnicaId": ficha["id"],
        "quantidadeProduzida": 980,
        "dataProcessamento": "2026-10-16",
        "itensUtilizados": [{"itemId": ingrediente, "quantidadeUtilizada": 523}],
        "etapasRealizadas": [{"etapaFichaTecnicaId": etapa_id, "tempoRealizadoMinutos": 12, "observacao": "Real"}],
    })
    assert response.status_code == 201
    body = response.get_json()
    assert float(body["quantidadeProduzida"]) == 980
    assert body["etapasRealizadas"][0]["tempoRealizadoMinutos"] == 12
