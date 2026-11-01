from app.extensions import db
from app.exceptions import (
    BusinessRuleException,
    FichaTecnicaNotFoundException,
    ItemNotFoundException,
    ProcessamentoNotFoundException,
)
from app.models import (
    EtapaProcessamento,
    FichaTecnica,
    Item,
    ItemProcessamento,
    Processamento,
    TipoItem,
)


class ProcessamentoService:
    @staticmethod
    def create(data):
        produto, ficha = ProcessamentoService._validate_base(data)

        processamento = Processamento(
            produto=produto,
            produto_id=produto.id,
            ficha_tecnica=ficha,
            ficha_tecnica_id=ficha.id,
            quantidade_produzida=data["quantidade_produzida"],
            data_processamento=data["data_processamento"],
        )
        ProcessamentoService._replace_children(processamento, ficha, data)
        db.session.add(processamento)
        db.session.commit()
        return processamento

    @staticmethod
    def get_all():
        return db.session.scalars(db.select(Processamento).order_by(Processamento.id)).all()

    @staticmethod
    def get_by_id(processamento_id):
        return ProcessamentoService._find(processamento_id)

    @staticmethod
    def update(processamento_id, data):
        processamento = ProcessamentoService._find(processamento_id)
        produto, ficha = ProcessamentoService._validate_base(data)

        processamento.produto = produto
        processamento.produto_id = produto.id
        processamento.ficha_tecnica = ficha
        processamento.ficha_tecnica_id = ficha.id
        processamento.quantidade_produzida = data["quantidade_produzida"]
        processamento.data_processamento = data["data_processamento"]
        ProcessamentoService._replace_children(processamento, ficha, data)
        db.session.commit()
        return processamento

    @staticmethod
    def delete(processamento_id):
        processamento = ProcessamentoService._find(processamento_id)
        db.session.delete(processamento)
        db.session.commit()

    @staticmethod
    def _validate_base(data):
        produto = ProcessamentoService._find_item(data["produto_id"])
        ficha = ProcessamentoService._find_ficha(data["ficha_tecnica_id"])

        if produto.tipo == TipoItem.INSUMO:
            raise BusinessRuleException(
                "Um item do tipo INSUMO não pode ser produto de um processamento"
            )
        if ficha.produto_id != produto.id:
            raise BusinessRuleException(
                "A ficha técnica informada não pertence ao produto do processamento"
            )

        etapa_ids = [e["etapa_ficha_tecnica_id"] for e in data["etapas_realizadas"]]
        if len(etapa_ids) != len(set(etapa_ids)):
            raise BusinessRuleException(
                "A mesma etapa da ficha técnica não pode ser registrada duas vezes no processamento"
            )
        for etapa_id in etapa_ids:
            ProcessamentoService._find_etapa_na_ficha(ficha, etapa_id)

        return produto, ficha

    @staticmethod
    def _replace_children(processamento, ficha, data):
        processamento.itens_utilizados.clear()
        for item_data in data["itens_utilizados"]:
            item = ProcessamentoService._find_item(item_data["item_id"])
            processamento.itens_utilizados.append(
                ItemProcessamento(
                    item=item,
                    item_id=item.id,
                    quantidade_utilizada=item_data["quantidade_utilizada"],
                )
            )

        processamento.etapas_realizadas.clear()
        for etapa_data in data["etapas_realizadas"]:
            planejada = ProcessamentoService._find_etapa_na_ficha(
                ficha, etapa_data["etapa_ficha_tecnica_id"]
            )
            processamento.etapas_realizadas.append(
                EtapaProcessamento(
                    etapa_ficha_tecnica_id_origem=planejada.id,
                    ordem=planejada.ordem,
                    nome=planejada.nome,
                    tipo_metodo=planejada.tipo_metodo,
                    tempo_previsto_minutos=planejada.tempo_previsto_minutos,
                    tempo_realizado_minutos=etapa_data["tempo_realizado_minutos"],
                    observacao=etapa_data.get("observacao"),
                )
            )

    @staticmethod
    def _find_etapa_na_ficha(ficha, etapa_id):
        for etapa in ficha.etapas:
            if etapa.id == etapa_id:
                return etapa
        raise BusinessRuleException(
            f"A etapa {etapa_id} não pertence à ficha técnica informada"
        )

    @staticmethod
    def _find(processamento_id):
        processamento = db.session.get(Processamento, processamento_id)
        if processamento is None:
            raise ProcessamentoNotFoundException(
                f"Processamento não encontrado: {processamento_id}"
            )
        return processamento

    @staticmethod
    def _find_item(item_id):
        item = db.session.get(Item, item_id)
        if item is None:
            raise ItemNotFoundException(f"Item não encontrado: {item_id}")
        return item

    @staticmethod
    def _find_ficha(ficha_id):
        ficha = db.session.get(FichaTecnica, ficha_id)
        if ficha is None:
            raise FichaTecnicaNotFoundException(f"Ficha técnica não encontrada: {ficha_id}")
        return ficha
