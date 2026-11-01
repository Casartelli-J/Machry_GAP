from datetime import date
from decimal import Decimal

from sqlalchemy import Date, ForeignKey, Numeric
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.extensions import db


class Processamento(db.Model):
    __tablename__ = "processamento"

    id: Mapped[int] = mapped_column(primary_key=True)
    produto_id: Mapped[int] = mapped_column(ForeignKey("item.id"), nullable=False)
    ficha_tecnica_id: Mapped[int] = mapped_column(
        ForeignKey("ficha_tecnica.id"), nullable=False
    )
    quantidade_produzida: Mapped[Decimal] = mapped_column(Numeric(18, 4), nullable=False)
    data_processamento: Mapped[date] = mapped_column(Date, nullable=False)

    produto = relationship("Item")
    ficha_tecnica = relationship("FichaTecnica")
    itens_utilizados = relationship(
        "ItemProcessamento",
        back_populates="processamento",
        cascade="all, delete-orphan",
        lazy="selectin",
    )
    etapas_realizadas = relationship(
        "EtapaProcessamento",
        back_populates="processamento",
        cascade="all, delete-orphan",
        order_by="EtapaProcessamento.ordem",
        lazy="selectin",
    )
