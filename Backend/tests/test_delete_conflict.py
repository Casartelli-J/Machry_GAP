def test_delete_item_em_uso_retorna_409(client):
    item = client.post(
        "/items", json={"nome": "Farinha", "tipo": "INSUMO", "unidadeMedida": "G"}
    ).get_json()
    lote = client.post("/lotes", json={
        "itemId": item["id"],
        "quantidade": 10,
        "dataProducao": "2026-10-01",
        "dataEntrada": "2026-10-01",
        "dataValidade": "2026-12-01",
    })
    assert lote.status_code == 201

    response = client.delete(f"/items/{item['id']}")
    assert response.status_code == 409
