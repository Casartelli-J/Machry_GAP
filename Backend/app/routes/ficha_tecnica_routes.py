from flask.views import MethodView
from flask_smorest import Blueprint

from app.schemas import FichaTecnicaRequestSchema, FichaTecnicaResponseSchema
from app.schemas.error import ApiErrorResponseSchema
from app.services import FichaTecnicaService

blp = Blueprint(
    "FichasTecnicas",
    "fichas-tecnicas",
    url_prefix="/fichas-tecnicas",
    description="Planejamento técnico da produção",
)


@blp.route("")
class FichaCollection(MethodView):
    @blp.response(200, FichaTecnicaResponseSchema(many=True))
    def get(self):
        return FichaTecnicaService.get_all()

    @blp.arguments(FichaTecnicaRequestSchema, error_status_code=400)
    @blp.response(201, FichaTecnicaResponseSchema)
    @blp.alt_response(400, schema=ApiErrorResponseSchema)
    def post(self, payload):
        return FichaTecnicaService.create(payload)


@blp.route("/<int:ficha_id>")
class FichaResource(MethodView):
    @blp.response(200, FichaTecnicaResponseSchema)
    def get(self, ficha_id):
        return FichaTecnicaService.get_by_id(ficha_id)

    @blp.arguments(FichaTecnicaRequestSchema, error_status_code=400)
    @blp.response(200, FichaTecnicaResponseSchema)
    def put(self, payload, ficha_id):
        return FichaTecnicaService.update(ficha_id, payload)

    @blp.response(204)
    @blp.alt_response(409, schema=ApiErrorResponseSchema)
    def delete(self, ficha_id):
        FichaTecnicaService.delete(ficha_id)
        return None
