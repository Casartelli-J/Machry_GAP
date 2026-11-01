from flask.views import MethodView
from flask_smorest import Blueprint

from app.schemas import LoteRequestSchema, LoteResponseSchema
from app.schemas.error import ApiErrorResponseSchema
from app.services import LoteService

blp = Blueprint("Lotes", "lotes", url_prefix="/lotes", description="CRUD de lotes")


@blp.route("")
class LoteCollection(MethodView):
    @blp.response(200, LoteResponseSchema(many=True))
    def get(self):
        return LoteService.get_all()

    @blp.arguments(LoteRequestSchema, error_status_code=400)
    @blp.response(201, LoteResponseSchema)
    def post(self, payload):
        return LoteService.create(payload)


@blp.route("/<int:lote_id>")
class LoteResource(MethodView):
    @blp.response(200, LoteResponseSchema)
    def get(self, lote_id):
        return LoteService.get_by_id(lote_id)

    @blp.arguments(LoteRequestSchema, error_status_code=400)
    @blp.response(200, LoteResponseSchema)
    def put(self, payload, lote_id):
        return LoteService.update(lote_id, payload)

    @blp.response(204)
    @blp.alt_response(409, schema=ApiErrorResponseSchema)
    def delete(self, lote_id):
        LoteService.delete(lote_id)
        return None
