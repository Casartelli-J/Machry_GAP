from app.extensions import db
from app.exceptions import BusinessRuleException, ItemNotFoundException, LoteNotFoundException
from app.models import Item, Lote


class LoteService:
    @staticmethod
    def create(data):
        LoteService._validate_dates(data)
        item = LoteService._find_item(data["item_id"])
        lote = Lote(item=item, **data)
        db.session.add(lote)
        db.session.commit()
        return lote

    @staticmethod
    def get_all():
        return db.session.scalars(db.select(Lote).order_by(Lote.id)).all()

    @staticmethod
    def get_by_id(lote_id):
        return LoteService._find(lote_id)

    @staticmethod
    def update(lote_id, data):
        LoteService._validate_dates(data)
        lote = LoteService._find(lote_id)
        item = LoteService._find_item(data["item_id"])
        lote.item = item
        lote.item_id = data["item_id"]
        lote.quantidade = data["quantidade"]
        lote.data_producao = data["data_producao"]
        lote.data_entrada = data["data_entrada"]
        lote.data_validade = data["data_validade"]
        db.session.commit()
        return lote

    @staticmethod
    def delete(lote_id):
        lote = LoteService._find(lote_id)
        db.session.delete(lote)
        db.session.commit()

    @staticmethod
    def _find(lote_id):
        lote = db.session.get(Lote, lote_id)
        if lote is None:
            raise LoteNotFoundException(f"Lote não encontrado: {lote_id}")
        return lote

    @staticmethod
    def _find_item(item_id):
        item = db.session.get(Item, item_id)
        if item is None:
            raise ItemNotFoundException(f"Item não encontrado: {item_id}")
        return item

    @staticmethod
    def _validate_dates(data):
        if data["data_validade"] < data["data_producao"]:
            raise BusinessRuleException(
                "A data de validade não pode ser anterior à data de produção"
            )
