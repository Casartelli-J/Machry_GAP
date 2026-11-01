from flask.views import MethodView
from flask_smorest import Blueprint

from app.schemas import ProcessamentoRequestSchema, ProcessamentoResponseSchema
from app.schemas.error import ApiErrorResponseSchema
from app.services import ProcessamentoService

blp = Blueprint(
    "Processamentos",
    "processamentos",
    url_prefix="/processamentos",
    description="Registro da produção realmente executada",
)


@blp.route("")
class ProcessamentoCollection(MethodView):
    @blp.response(200, ProcessamentoResponseSchema(many=True))
    def get(self):
        return ProcessamentoService.get_all()

    @blp.arguments(ProcessamentoRequestSchema, error_status_code=400)
    @blp.response(201, ProcessamentoResponseSchema)
    @blp.alt_response(400, schema=ApiErrorResponseSchema)
    def post(self, payload):
        return ProcessamentoService.create(payload)


@blp.route("/<int:processamento_id>")
class ProcessamentoResource(MethodView):
    @blp.response(200, ProcessamentoResponseSchema)
    def get(self, processamento_id):
        return ProcessamentoService.get_by_id(processamento_id)

    @blp.arguments(ProcessamentoRequestSchema, error_status_code=400)
    @blp.response(200, ProcessamentoResponseSchema)
    def put(self, payload, processamento_id):
        return ProcessamentoService.update(processamento_id, payload)

    @blp.response(204)
    def delete(self, processamento_id):
        ProcessamentoService.delete(processamento_id)
        return None
