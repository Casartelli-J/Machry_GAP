
import psycopg
import pytest
from psycopg import sql
from sqlalchemy.engine import make_url

from app import create_app
from app.extensions import db
from config import Config, TestingConfig


def _ensure_safe_test_database():
    """Cria machry_test se necessário e impede testes no banco de desenvolvimento."""
    test_url = make_url(TestingConfig.SQLALCHEMY_DATABASE_URI)
    dev_url = make_url(Config.SQLALCHEMY_DATABASE_URI)

    if test_url.database == dev_url.database:
        raise RuntimeError(
            "TEST_DATABASE_URL não pode apontar para o mesmo banco de DATABASE_URL. "
            "Use um banco separado, como machry_test."
        )

    if not test_url.database:
        raise RuntimeError("TEST_DATABASE_URL precisa informar o nome do banco de testes.")

    # Conecta no banco administrativo 'postgres' para criar o banco de testes.
    with psycopg.connect(
        dbname="postgres",
        user=test_url.username,
        password=test_url.password,
        host=test_url.host or "localhost",
        port=test_url.port or 5432,
        autocommit=True,
    ) as connection:
        with connection.cursor() as cursor:
            cursor.execute(
                "SELECT 1 FROM pg_database WHERE datname = %s",
                (test_url.database,),
            )
            if cursor.fetchone() is None:
                cursor.execute(
                    sql.SQL("CREATE DATABASE {}").format(
                        sql.Identifier(test_url.database)
                    )
                )


@pytest.fixture(scope="session", autouse=True)
def test_database():
    _ensure_safe_test_database()
    yield


@pytest.fixture()
def app(test_database):
    application = create_app(TestingConfig)
    with application.app_context():
        db.drop_all()
        db.create_all()
        yield application
        db.session.remove()
        db.drop_all()


@pytest.fixture()
def client(app):
    return app.test_client()
