from sqlalchemy import Enum, ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.extensions import db
from app.models.enums import TipoMetodo


class EtapaProcessamento(db.Model):
    __tablename__ = "etapa_processamento"

    id: Mapped[int] = mapped_column(primary_key=True)
    processamento_id: Mapped[int] = mapped_column(
        ForeignKey("processamento.id", ondelete="CASCADE"), nullable=False
    )

    # Snapshot histórico: propositalmente NÃO é FK para etapa_ficha_tecnica.
    etapa_ficha_tecnica_id_origem: Mapped[int] = mapped_column(nullable=False)
    ordem: Mapped[int] = mapped_column(nullable=False)
    nome: Mapped[str] = mapped_column(String(255), nullable=False)
    tipo_metodo: Mapped[TipoMetodo] = mapped_column(
        Enum(TipoMetodo, native_enum=False, length=30), nullable=False
    )
    tempo_previsto_minutos: Mapped[int] = mapped_column(nullable=False)
    tempo_realizado_minutos: Mapped[int] = mapped_column(nullable=False)
    observacao: Mapped[str | None] = mapped_column(Text, nullable=True)

    processamento = relationship("Processamento", back_populates="etapas_realizadas")
