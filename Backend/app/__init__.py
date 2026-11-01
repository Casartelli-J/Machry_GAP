from flask import Flask, jsonify
from dotenv import load_dotenv

from config import Config
from app.extensions import api, db


def create_app(config_object=Config):
    load_dotenv()

    app = Flask(__name__)
    app.config.from_object(config_object)

    db.init_app(app)
    api.init_app(app)

    # Importa os models antes do create_all para que o metadata conheça as tabelas.
    from app import models  # noqa: F401
    from app.routes import register_blueprints
    from app.exceptions.handlers import register_error_handlers

    register_blueprints(api)
    register_error_handlers(app)

    @app.get("/health")
    def health():
        return jsonify({"status": "ok", "application": "machry-api-flask"}), 200

    if app.config.get("AUTO_CREATE_DB"):
        with app.app_context():
            db.create_all()

    return app
