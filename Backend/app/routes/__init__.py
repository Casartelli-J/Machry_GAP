from app.routes.item_routes import blp as item_blp
from app.routes.lote_routes import blp as lote_blp
from app.routes.ficha_tecnica_routes import blp as ficha_blp
from app.routes.processamento_routes import blp as processamento_blp


def register_blueprints(api):
    api.register_blueprint(item_blp)
    api.register_blueprint(lote_blp)
    api.register_blueprint(ficha_blp)
    api.register_blueprint(processamento_blp)
