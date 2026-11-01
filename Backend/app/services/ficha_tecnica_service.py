from app.extensions import db
from app.exceptions import (
    BusinessRuleException,
    FichaTecnicaNotFoundException,
    ItemNotFoundException,
)
from app.models import (
    EtapaFichaTecnica,
    FichaTecnica,
    IngredienteFicha,
    Item,
    TipoItem,
)


class FichaTecnicaService:
    @staticmethod
    def create(data):
        produto = FichaTecnicaService._find_item(data["produto_id"])
        FichaTecnicaService._validate(produto, data, ficha_id_atual=None)

        ficha = FichaTecnica(
            produto=produto,
            produto_id=produto.id,
            rendimento=data["rendimento"],
            unidade_rendimento=data["unidade_rendimento"],
        )
        FichaTecnicaService._replace_children(ficha, data)
        db.session.add(ficha)
        db.session.commit()
        return ficha

    @staticmethod
    def get_all():
        return db.session.scalars(db.select(FichaTecnica).order_by(FichaTecnica.id)).all()

    @staticmethod
    def get_by_id(ficha_id):
        return FichaTecnicaService._find(ficha_id)

    @staticmethod
    def update(ficha_id, data):
        ficha = FichaTecnicaService._find(ficha_id)
        produto = FichaTecnicaService._find_item(data["produto_id"])
        FichaTecnicaService._validate(produto, data, ficha_id_atual=ficha_id)

        ficha.produto = produto
        ficha.produto_id = produto.id
        ficha.rendimento = data["rendimento"]
        ficha.unidade_rendimento = data["unidade_rendimento"]
        FichaTecnicaService._replace_children(ficha, data)
        db.session.commit()
        return ficha

    @staticmethod
    def delete(ficha_id):
        ficha = FichaTecnicaService._find(ficha_id)
        db.session.delete(ficha)
        db.session.commit()

    @staticmethod
    def _replace_children(ficha, data):
        ficha.ingredientes.clear()
        for ingrediente in data["ingredientes"]:
            item = FichaTecnicaService._find_item(ingrediente["item_id"])
            ficha.ingredientes.append(
                IngredienteFicha(item=item, item_id=item.id, quantidade=ingrediente["quantidade"])
            )

        ficha.etapas.clear()
        for etapa in data["etapas"]:
            ficha.etapas.append(EtapaFichaTecnica(**etapa))

    @staticmethod
    def _validate(produto, data, ficha_id_atual):
        if produto.tipo == TipoItem.INSUMO:
            raise BusinessRuleException("Um item do tipo INSUMO não pode possuir ficha técnica")

        query = db.select(FichaTecnica).where(FichaTecnica.produto_id == produto.id)
        if ficha_id_atual is not None:
            query = query.where(FichaTecnica.id != ficha_id_atual)
        if db.session.scalar(query) is not None:
            raise BusinessRuleException("O produto já possui uma ficha técnica cadastrada")

        if any(i["item_id"] == produto.id for i in data["ingredientes"]):
            raise BusinessRuleException(
                "O produto não pode ser ingrediente da própria ficha técnica"
            )

        ordens = [e["ordem"] for e in data["etapas"]]
        if len(ordens) != len(set(ordens)):
            raise BusinessRuleException(
                "Não pode haver duas etapas com a mesma ordem na ficha técnica"
            )

    @staticmethod
    def _find(ficha_id):
        ficha = db.session.get(FichaTecnica, ficha_id)
        if ficha is None:
            raise FichaTecnicaNotFoundException(f"Ficha técnica não encontrada: {ficha_id}")
        return ficha

    @staticmethod
    def _find_item(item_id):
        item = db.session.get(Item, item_id)
        if item is None:
            raise ItemNotFoundException(f"Item não encontrado: {item_id}")
        return item
