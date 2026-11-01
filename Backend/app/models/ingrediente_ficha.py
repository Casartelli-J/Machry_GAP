from decimal import Decimal

from sqlalchemy import ForeignKey, Numeric
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.extensions import db


class IngredienteFicha(db.Model):
    __tablename__ = "ingrediente_ficha"

    id: Mapped[int] = mapped_column(primary_key=True)
    ficha_tecnica_id: Mapped[int] = mapped_column(
        ForeignKey("ficha_tecnica.id", ondelete="CASCADE"), nullable=False
    )
    item_id: Mapped[int] = mapped_column(ForeignKey("item.id"), nullable=False)
    quantidade: Mapped[Decimal] = mapped_column(Numeric(18, 4), nullable=False)

    ficha_tecnica = relationship("FichaTecnica", back_populates="ingredientes")
    item = relationship("Item")
