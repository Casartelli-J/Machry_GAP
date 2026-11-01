from enum import Enum


class TipoItem(str, Enum):
    INSUMO = "INSUMO"
    INTERMEDIARIO = "INTERMEDIARIO"
    FINAL = "FINAL"


class TipoMetodo(str, Enum):
    PREPARO = "PREPARO"
    COCCAO = "COCCAO"
    MONTAGEM = "MONTAGEM"
    RESFRIAMENTO = "RESFRIAMENTO"
