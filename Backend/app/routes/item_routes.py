from flask.views import MethodView
from flask_smorest import Blueprint

from app.schemas import ItemRequestSchema, ItemResponseSchema
from app.schemas.error import ApiErrorResponseSchema
from app.services import ItemService

blp = Blueprint("Items", "items", url_prefix="/items", description="CRUD de itens")


@blp.route("")
class ItemCollection(MethodView):
    @blp.response(200, ItemResponseSchema(many=True))
    def get(self):
        """Lista todos os itens."""
        return ItemService.get_all()

    @blp.arguments(ItemRequestSchema, error_status_code=400)
    @blp.response(201, ItemResponseSchema)
    @blp.alt_response(400, schema=ApiErrorResponseSchema)
    def post(self, payload):
        """Cria um item."""
        return ItemService.create(payload)


@blp.route("/<int:item_id>")
class ItemResource(MethodView):
    @blp.response(200, ItemResponseSchema)
    @blp.alt_response(404, schema=ApiErrorResponseSchema)
    def get(self, item_id):
        return ItemService.get_by_id(item_id)

    @blp.arguments(ItemRequestSchema, error_status_code=400)
    @blp.response(200, ItemResponseSchema)
    def put(self, payload, item_id):
        return ItemService.update(item_id, payload)

    @blp.response(204)
    @blp.alt_response(404, schema=ApiErrorResponseSchema)
    @blp.alt_response(409, schema=ApiErrorResponseSchema)
    def delete(self, item_id):
        ItemService.delete(item_id)
        return None
