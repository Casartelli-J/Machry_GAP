from datetime import datetime, timezone

from flask import jsonify, request
from sqlalchemy.exc import IntegrityError
from werkzeug.exceptions import HTTPException

from app.extensions import db
from app.exceptions.exceptions import (
    BusinessRuleException,
    FichaTecnicaNotFoundException,
    ItemNotFoundException,
    LoteNotFoundException,
    ProcessamentoNotFoundException,
    ResourceInUseException,
)


def _flatten_messages(messages, prefix=""):
    result = {}
    if isinstance(messages, dict):
        for key, value in messages.items():
            path = f"{prefix}.{key}" if prefix else str(key)
            result.update(_flatten_messages(value, path))
    elif isinstance(messages, list):
        if all(isinstance(item, str) for item in messages):
            result[prefix or "request"] = "; ".join(messages)
        else:
            for index, value in enumerate(messages):
                path = f"{prefix}[{index}]" if prefix else f"[{index}]"
                result.update(_flatten_messages(value, path))
    else:
        result[prefix or "request"] = str(messages)
    return result


def _response(status, error, message, fields=None):
    return jsonify({
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "status": status,
        "error": error,
        "message": message,
        "path": request.path,
        "fields": fields or {},
    }), status


def register_error_handlers(app):
    @app.errorhandler(BusinessRuleException)
    def handle_business_rule(exc):
        return _response(400, "Bad Request", str(exc))

    @app.errorhandler(ResourceInUseException)
    def handle_resource_in_use(exc):
        return _response(409, "Conflict", str(exc))

    for exc_type in (
        ItemNotFoundException,
        LoteNotFoundException,
        FichaTecnicaNotFoundException,
        ProcessamentoNotFoundException,
    ):
        app.register_error_handler(
            exc_type,
            lambda exc: _response(404, "Not Found", str(exc)),
        )

    @app.errorhandler(IntegrityError)
    def handle_integrity_error(exc):
        db.session.rollback()
        return _response(
            409,
            "Conflict",
            "O recurso não pode ser alterado ou excluído porque está sendo utilizado por outros registros.",
        )

    @app.errorhandler(HTTPException)
    def handle_http_exception(exc):
        data = getattr(exc, "data", {}) or {}
        messages = data.get("messages") or {}
        fields = _flatten_messages(messages) if messages else {}
        message = data.get("message") or exc.description
        return _response(exc.code or 500, exc.name, message, fields)

    @app.errorhandler(Exception)
    def handle_unexpected_error(exc):
        db.session.rollback()
        app.logger.exception("Erro inesperado", exc_info=exc)
        return _response(500, "Internal Server Error", "Erro interno inesperado")
