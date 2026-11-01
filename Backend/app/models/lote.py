from datetime import date
from decimal import Decimal

from sqlalchemy import Date, ForeignKey, Numeric
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.extensions import db


class Lote(db.Model):
    __tablename__ = "lote"

    id: Mapped[int] = mapped_column(primary_key=True)
    item_id: Mapped[int] = mapped_column(ForeignKey("item.id"), nullable=False)
    quantidade: Mapped[Decimal] = mapped_column(Numeric(18, 4), nullable=False)
    data_producao: Mapped[date] = mapped_column(Date, nullable=False)
    data_entrada: Mapped[date] = mapped_column(Date, nullable=False)
    data_validade: Mapped[date] = mapped_column(Date, nullable=False)

    item = relationship("Item")
