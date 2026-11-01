from flask_sqlalchemy import SQLAlchemy
from flask_smorest import Api
from sqlalchemy.orm import DeclarativeBase


class Base(DeclarativeBase):
    pass


db = SQLAlchemy(model_class=Base)
api = Api()
