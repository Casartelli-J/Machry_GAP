from decimal import Decimal

from sqlalchemy import ForeignKey, Numeric
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.extensions import db


class ItemProcessamento(db.Model):
    __tablename__ = "item_processamento"

    id: Mapped[int] = mapped_column(primary_key=True)
    processamento_id: Mapped[int] = mapped_column(
        ForeignKey("processamento.id", ondelete="CASCADE"), nullable=False
    )
    item_id: Mapped[int] = mapped_column(ForeignKey("item.id"), nullable=False)
    quantidade_utilizada: Mapped[Decimal] = mapped_column(Numeric(18, 4), nullable=False)

    processamento = relationship("Processamento", back_populates="itens_utilizados")
    item = relationship("Item")
