from marshmallow import Schema, fields

from app.schemas.validators import positive


class LoteRequestSchema(Schema):
    item_id = fields.Int(data_key="itemId", required=True)
    quantidade = fields.Decimal(required=True, as_string=False, validate=positive)
    data_producao = fields.Date(data_key="dataProducao", required=True)
    data_entrada = fields.Date(data_key="dataEntrada", required=True)
    data_validade = fields.Date(data_key="dataValidade", required=True)


class LoteResponseSchema(Schema):
    id = fields.Int()
    item_id = fields.Int(data_key="itemId")
    item_nome = fields.Method("get_item_nome", data_key="itemNome")
    quantidade = fields.Decimal(as_string=False)
    data_producao = fields.Date(data_key="dataProducao")
    data_entrada = fields.Date(data_key="dataEntrada")
    data_validade = fields.Date(data_key="dataValidade")

    def get_item_nome(self, obj):
        return obj.item.nome
