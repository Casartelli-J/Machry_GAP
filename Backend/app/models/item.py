from sqlalchemy import Enum, String
from sqlalchemy.orm import Mapped, mapped_column

from app.extensions import db
from app.models.enums import TipoItem


class Item(db.Model):
    __tablename__ = "item"

    id: Mapped[int] = mapped_column(primary_key=True)
    nome: Mapped[str] = mapped_column(String(255), nullable=False)
    tipo: Mapped[TipoItem] = mapped_column(
        Enum(TipoItem, native_enum=False, length=30), nullable=False
    )
    unidade_medida: Mapped[str] = mapped_column(String(50), nullable=False)
