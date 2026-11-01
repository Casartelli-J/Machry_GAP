def test_item_crud_http_status(client):
    payload = {"nome": "Farinha", "tipo": "INSUMO", "unidadeMedida": "G"}

    created = client.post("/items", json=payload)
    assert created.status_code == 201
    item_id = created.get_json()["id"]

    assert client.get("/items").status_code == 200
    assert client.get(f"/items/{item_id}").status_code == 200

    updated = client.put(
        f"/items/{item_id}",
        json={"nome": "Farinha de trigo", "tipo": "INSUMO", "unidadeMedida": "G"},
    )
    assert updated.status_code == 200

    assert client.delete(f"/items/{item_id}").status_code == 204
    assert client.get(f"/items/{item_id}").status_code == 404


def test_item_validation_returns_400(client):
    response = client.post("/items", json={"nome": "", "tipo": "INSUMO"})
    assert response.status_code == 400
    assert response.get_json()["status"] == 400
