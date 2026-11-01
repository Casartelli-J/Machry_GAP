def create_item(client, nome, tipo, unidade="G"):
    return client.post(
        "/items", json={"nome": nome, "tipo": tipo, "unidadeMedida": unidade}
    ).get_json()["id"]


def test_ficha_tecnica_fluxo(client):
    farinha = create_item(client, "Farinha", "INSUMO")
    massa = create_item(client, "Massa", "INTERMEDIARIO")

    payload = {
        "produtoId": massa,
        "rendimento": 1000,
        "unidadeRendimento": "G",
        "ingredientes": [{"itemId": farinha, "quantidade": 500}],
        "etapas": [{
            "ordem": 1,
            "nome": "Misturar",
            "tipoMetodo": "PREPARO",
            "descricao": "Misturar ingredientes",
            "tempoPrevistoMinutos": 10,
        }],
    }
    created = client.post("/fichas-tecnicas", json=payload)
    assert created.status_code == 201
    ficha_id = created.get_json()["id"]
    assert client.get(f"/fichas-tecnicas/{ficha_id}").status_code == 200


def test_insumo_nao_pode_ter_ficha(client):
    insumo = create_item(client, "Farinha", "INSUMO")
    outro = create_item(client, "Água", "INSUMO", "ML")
    response = client.post("/fichas-tecnicas", json={
        "produtoId": insumo,
        "rendimento": 1,
        "unidadeRendimento": "G",
        "ingredientes": [{"itemId": outro, "quantidade": 1}],
        "etapas": [{"ordem": 1, "nome": "Teste", "tipoMetodo": "PREPARO", "tempoPrevistoMinutos": 1}],
    })
    assert response.status_code == 400
