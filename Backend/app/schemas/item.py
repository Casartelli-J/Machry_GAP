from marshmallow import Schema, fields

from app.models.enums import TipoItem
from app.schemas.validators import not_blank


class ItemRequestSchema(Schema):
    nome = fields.String(required=True, validate=not_blank)
    tipo = fields.Enum(TipoItem, by_value=True, required=True)
    unidade_medida = fields.String(
        data_key="unidadeMedida", required=True, validate=not_blank
    )


class ItemResponseSchema(Schema):
    id = fields.Int(dump_only=True)
    nome = fields.String()
    tipo = fields.Enum(TipoItem, by_value=True)
    unidade_medida = fields.String(data_key="unidadeMedida")
