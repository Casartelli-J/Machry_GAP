from marshmallow import Schema, fields


class ApiErrorResponseSchema(Schema):
    timestamp = fields.String()
    status = fields.Int()
    error = fields.String()
    message = fields.String()
    path = fields.String()
    fields = fields.Dict(keys=fields.String(), values=fields.String())
