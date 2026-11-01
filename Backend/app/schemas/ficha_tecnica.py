from marshmallow import Schema, fields

from app.models.enums import TipoMetodo
from app.schemas.validators import not_blank, positive


class IngredienteFichaRequestSchema(Schema):
    item_id = fields.Int(data_key="itemId", required=True)
    quantidade = fields.Decimal(required=True, as_string=False, validate=positive)


class IngredienteFichaResponseSchema(Schema):
    id = fields.Int()
    item_id = fields.Int(data_key="itemId")
    item_nome = fields.Method("get_item_nome", data_key="itemNome")
    quantidade = fields.Decimal(as_string=False)
    unidade_medida = fields.Method("get_unidade", data_key="unidadeMedida")

    def get_item_nome(self, obj):
        return obj.item.nome

    def get_unidade(self, obj):
        return obj.item.unidade_medida


class EtapaFichaTecnicaRequestSchema(Schema):
    ordem = fields.Int(required=True, validate=positive)
    nome = fields.String(required=True, validate=not_blank)
    tipo_metodo = fields.Enum(
        TipoMetodo, by_value=True, data_key="tipoMetodo", required=True
    )
    descricao = fields.String(load_default=None, allow_none=True)
    tempo_previsto_minutos = fields.Int(
        data_key="tempoPrevistoMinutos", required=True, validate=positive
    )


class EtapaFichaTecnicaResponseSchema(Schema):
    id = fields.Int()
    ordem = fields.Int()
    nome = fields.String()
    tipo_metodo = fields.Enum(TipoMetodo, by_value=True, data_key="tipoMetodo")
    descricao = fields.String(allow_none=True)
    tempo_previsto_minutos = fields.Int(data_key="tempoPrevistoMinutos")


class FichaTecnicaRequestSchema(Schema):
    produto_id = fields.Int(data_key="produtoId", required=True)
    rendimento = fields.Decimal(required=True, as_string=False, validate=positive)
    unidade_rendimento = fields.String(
        data_key="unidadeRendimento", required=True, validate=not_blank
    )
    ingredientes = fields.List(
        fields.Nested(IngredienteFichaRequestSchema), required=True, validate=lambda v: bool(v)
    )
    etapas = fields.List(
        fields.Nested(EtapaFichaTecnicaRequestSchema), required=True, validate=lambda v: bool(v)
    )


class FichaTecnicaResponseSchema(Schema):
    id = fields.Int()
    produto_id = fields.Int(data_key="produtoId")
    produto_nome = fields.Method("get_produto_nome", data_key="produtoNome")
    rendimento = fields.Decimal(as_string=False)
    unidade_rendimento = fields.String(data_key="unidadeRendimento")
    ingredientes = fields.List(fields.Nested(IngredienteFichaResponseSchema))
    etapas = fields.List(fields.Nested(EtapaFichaTecnicaResponseSchema))

    def get_produto_nome(self, obj):
        return obj.produto.nome
