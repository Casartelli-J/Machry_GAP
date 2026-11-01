from decimal import Decimal

from sqlalchemy import ForeignKey, Numeric, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.extensions import db


class FichaTecnica(db.Model):
    __tablename__ = "ficha_tecnica"

    id: Mapped[int] = mapped_column(primary_key=True)
    produto_id: Mapped[int] = mapped_column(
        ForeignKey("item.id"), nullable=False, unique=True
    )
    rendimento: Mapped[Decimal] = mapped_column(Numeric(18, 4), nullable=False)
    unidade_rendimento: Mapped[str] = mapped_column(String(50), nullable=False)

    produto = relationship("Item")
    ingredientes = relationship(
        "IngredienteFicha",
        back_populates="ficha_tecnica",
        cascade="all, delete-orphan",
        lazy="selectin",
    )
    etapas = relationship(
        "EtapaFichaTecnica",
        back_populates="ficha_tecnica",
        cascade="all, delete-orphan",
        order_by="EtapaFichaTecnica.ordem",
        lazy="selectin",
    )
