def create_item(client):
    return client.post(
        "/items",
        json={"nome": "Leite", "tipo": "INSUMO", "unidadeMedida": "ML"},
    ).get_json()["id"]


def test_lote_crud(client):
    item_id = create_item(client)
    payload = {
        "itemId": item_id,
        "quantidade": 1000,
        "dataProducao": "2026-10-10",
        "dataEntrada": "2026-10-10",
        "dataValidade": "2026-11-10",
    }
    created = client.post("/lotes", json=payload)
    assert created.status_code == 201
    lote_id = created.get_json()["id"]
    assert client.get(f"/lotes/{lote_id}").status_code == 200
    assert client.put(f"/lotes/{lote_id}", json=payload).status_code == 200
    assert client.delete(f"/lotes/{lote_id}").status_code == 204


def test_lote_invalid_dates(client):
    item_id = create_item(client)
    response = client.post("/lotes", json={
        "itemId": item_id,
        "quantidade": 1,
        "dataProducao": "2026-10-10",
        "dataEntrada": "2026-10-10",
        "dataValidade": "2026-10-09",
    })
    assert response.status_code == 400
