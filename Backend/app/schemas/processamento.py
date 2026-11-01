from marshmallow import Schema, fields

from app.models.enums import TipoMetodo
from app.schemas.validators import positive


class ItemProcessamentoRequestSchema(Schema):
    item_id = fields.Int(data_key="itemId", required=True)
    quantidade_utilizada = fields.Decimal(
        data_key="quantidadeUtilizada", required=True, as_string=False, validate=positive
    )


class ItemProcessamentoResponseSchema(Schema):
    id = fields.Int()
    item_id = fields.Int(data_key="itemId")
    item_nome = fields.Method("get_item_nome", data_key="itemNome")
    quantidade_utilizada = fields.Decimal(
        data_key="quantidadeUtilizada", as_string=False
    )
    unidade_medida = fields.Method("get_unidade", data_key="unidadeMedida")

    def get_item_nome(self, obj):
        return obj.item.nome

    def get_unidade(self, obj):
        return obj.item.unidade_medida


class EtapaProcessamentoRequestSchema(Schema):
    etapa_ficha_tecnica_id = fields.Int(
        data_key="etapaFichaTecnicaId", required=True
    )
    tempo_realizado_minutos = fields.Int(
        data_key="tempoRealizadoMinutos", required=True, validate=positive
    )
    observacao = fields.String(load_default=None, allow_none=True)


class EtapaProcessamentoResponseSchema(Schema):
    id = fields.Int()
    etapa_ficha_tecnica_id_origem = fields.Int(data_key="etapaFichaTecnicaId")
    ordem = fields.Int()
    nome = fields.String()
    tipo_metodo = fields.Enum(TipoMetodo, by_value=True, data_key="tipoMetodo")
    tempo_previsto_minutos = fields.Int(data_key="tempoPrevistoMinutos")
    tempo_realizado_minutos = fields.Int(data_key="tempoRealizadoMinutos")
    observacao = fields.String(allow_none=True)


class ProcessamentoRequestSchema(Schema):
    produto_id = fields.Int(data_key="produtoId", required=True)
    ficha_tecnica_id = fields.Int(data_key="fichaTecnicaId", required=True)
    quantidade_produzida = fields.Decimal(
        data_key="quantidadeProduzida", required=True, as_string=False, validate=positive
    )
    data_processamento = fields.Date(data_key="dataProcessamento", required=True)
    itens_utilizados = fields.List(
        fields.Nested(ItemProcessamentoRequestSchema),
        data_key="itensUtilizados",
        required=True,
        validate=lambda v: bool(v),
    )
    etapas_realizadas = fields.List(
        fields.Nested(EtapaProcessamentoRequestSchema),
        data_key="etapasRealizadas",
        required=True,
        validate=lambda v: bool(v),
    )


class ProcessamentoResponseSchema(Schema):
    id = fields.Int()
    produto_id = fields.Int(data_key="produtoId")
    produto_nome = fields.Method("get_produto_nome", data_key="produtoNome")
    ficha_tecnica_id = fields.Int(data_key="fichaTecnicaId")
    quantidade_produzida = fields.Decimal(data_key="quantidadeProduzida", as_string=False)
    data_processamento = fields.Date(data_key="dataProcessamento")
    itens_utilizados = fields.List(
        fields.Nested(ItemProcessamentoResponseSchema), data_key="itensUtilizados"
    )
    etapas_realizadas = fields.List(
        fields.Nested(EtapaProcessamentoResponseSchema), data_key="etapasRealizadas"
    )

    def get_produto_nome(self, obj):
        return obj.produto.nome
