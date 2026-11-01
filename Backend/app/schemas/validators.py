from marshmallow import ValidationError


def not_blank(value: str) -> None:
    if not value or not value.strip():
        raise ValidationError("O campo não pode ficar em branco")


def positive(value) -> None:
    if value is None or value <= 0:
        raise ValidationError("O valor deve ser maior que zero")
