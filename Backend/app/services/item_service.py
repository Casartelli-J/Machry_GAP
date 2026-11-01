from app.extensions import db
from app.exceptions import ItemNotFoundException, ResourceInUseException
from app.models import (
    FichaTecnica,
    IngredienteFicha,
    Item,
    ItemProcessamento,
    Lote,
    Processamento,
)


class ItemService:
    @staticmethod
    def create(data):
        item = Item(**data)
        db.session.add(item)
        db.session.commit()
        return item

    @staticmethod
    def get_all():
        return db.session.scalars(db.select(Item).order_by(Item.id)).all()

    @staticmethod
    def get_by_id(item_id):
        return ItemService._find(item_id)

    @staticmethod
    def update(item_id, data):
        item = ItemService._find(item_id)
        item.nome = data["nome"]
        item.tipo = data["tipo"]
        item.unidade_medida = data["unidade_medida"]
        db.session.commit()
        return item

    @staticmethod
    def delete(item_id):
        item = ItemService._find(item_id)

        # A API não deve depender apenas da constraint do banco para descobrir
        # que um item está em uso. Isso mantém o comportamento 409 explícito e
        # consistente em todos os ambientes.
        dependencies = (
            (Lote.id, Lote.item_id),
            (IngredienteFicha.id, IngredienteFicha.item_id),
            (ItemProcessamento.id, ItemProcessamento.item_id),
            (FichaTecnica.id, FichaTecnica.produto_id),
            (Processamento.id, Processamento.produto_id),
        )

        for dependency_id, foreign_key in dependencies:
            in_use = db.session.scalar(
                db.select(dependency_id).where(foreign_key == item_id).limit(1)
            )
            if in_use is not None:
                raise ResourceInUseException(
                    "O item não pode ser excluído porque está sendo utilizado por outros registros."
                )

        db.session.delete(item)
        db.session.commit()

    @staticmethod
    def _find(item_id):
        item = db.session.get(Item, item_id)
        if item is None:
            raise ItemNotFoundException(f"Item não encontrado: {item_id}")
        return item
