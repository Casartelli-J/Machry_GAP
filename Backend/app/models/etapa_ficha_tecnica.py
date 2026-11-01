from sqlalchemy import Enum, ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.extensions import db
from app.models.enums import TipoMetodo


class EtapaFichaTecnica(db.Model):
    __tablename__ = "etapa_ficha_tecnica"

    id: Mapped[int] = mapped_column(primary_key=True)
    ficha_tecnica_id: Mapped[int] = mapped_column(
        ForeignKey("ficha_tecnica.id", ondelete="CASCADE"), nullable=False
    )
    ordem: Mapped[int] = mapped_column(nullable=False)
    nome: Mapped[str] = mapped_column(String(255), nullable=False)
    tipo_metodo: Mapped[TipoMetodo] = mapped_column(
        Enum(TipoMetodo, native_enum=False, length=30), nullable=False
    )
    descricao: Mapped[str | None] = mapped_column(Text, nullable=True)
    tempo_previsto_minutos: Mapped[int] = mapped_column(nullable=False)

    ficha_tecnica = relationship("FichaTecnica", back_populates="etapas")
