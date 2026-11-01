"""Regras de negócio do estoque, orquestrando o repository."""
from __future__ import annotations

from datetime import date, timedelta
from decimal import Decimal
from typing import Iterable, Optional

from app.utils.errors import Conflito, EstoqueInsuficiente, RegistroNaoEncontrado

from . import models, schemas
from .repository import EstoqueRepository

TIPOS_QUE_EXIGEM_SAIDA = {"SAIDA", "AJUSTE_SAIDA", "TRANSFERENCIA", "TRANSFORMACAO", "CONSUMO_PRODUCAO", "PERDA"}


class EstoqueService:
    def __init__(self, repository: EstoqueRepository) -> None:
        self.repository = repository

    def listar_inventario(
        self, produto_id: Optional[int] = None, lote_id: Optional[int] = None
    ) -> list[models.ItemInventario]:
        linhas = self.repository.listar_inventario(produto_id, lote_id)
        return [models.ItemInventario.from_row(r) for r in linhas]

    def listar_movimentacoes(
        self,
        limite: int = 100,
        lote_id: Optional[int] = None,
        usuario_id: Optional[int] = None,
        tipo: Optional[str] = None,
    ) -> list[models.MovimentacaoEstoque]:
        linhas = self.repository.listar_movimentacoes(limite, lote_id, usuario_id, tipo)
        return [models.MovimentacaoEstoque.from_row(r) for r in linhas]

    def registrar_movimentacao(
        self, dados: schemas.MovimentacaoCreate, *, usuario_id: int
    ) -> models.MovimentacaoEstoque:
        with self.repository.transacao():
            origem = self._obter_lote_ou_falhar(dados.lote_id, para_atualizacao=True)
            self._validar_usuario_ativo(usuario_id)
            self._validar_saldo_suficiente(origem, dados)
            destino = None
            if dados.lote_destino_id is not None:
                destino = self._validar_lote_destino(origem, dados)
            return self.repository.inserir_movimentacao(
                tipo=dados.tipo,
                lote_origem=origem,
                lote_destino=destino,
                quantidade=dados.quantidade,
                quantidade_destino=dados.quantidade_destino,
                motivo=dados.motivo,
                usuario_id=usuario_id,
                observacao=dados.observacao,
            )

    def sugestao_saida(self, produto_id: int, quantidade: float) -> models.SugestaoSaida:
        if self.repository.obter_produto_ativo(produto_id) is None:
            raise RegistroNaoEncontrado("produto não encontrado")
        faltante = quantidade
        selecao: list[models.LoteSugerido] = []
        for lote in self.repository.lotes_fefo_disponiveis(produto_id):
            usar = min(faltante, lote["quantidade"])
            if usar > 0:
                selecao.append(
                    models.LoteSugerido(
                        lote_id=lote["id"], quantidade=usar, data_validade=lote["data_validade"]
                    )
                )
                faltante -= usar
            if faltante <= 0:
                break
        return models.SugestaoSaida(
            produto_id=produto_id,
            quantidade_solicitada=quantidade,
            lotes=selecao,
            quantidade_faltante=max(0, faltante),
        )

    def consumir_componentes_producao(
        self, *, solicitacao_id: int, componentes: Iterable[object], usuario_id: int
    ) -> list[models.MovimentacaoEstoque]:
        """Baixa os componentes planejados usando FEFO dentro da transação do chamador.

        Este método propositalmente NÃO abre nem fecha transação: a conclusão da
        produção é responsável pelo commit único de status + estoque.
        """
        self._validar_usuario_ativo(usuario_id)
        if self.repository.existe_consumo_producao(solicitacao_id):
            raise Conflito("o consumo de estoque desta produção já foi registrado")

        # Primeiro montamos o plano inteiro mantendo os lotes relevantes bloqueados.
        # Só depois de provar que TODOS os componentes têm saldo suficiente alteramos
        # quantidades e criamos movimentos. Isso reduz trabalho antes de um eventual
        # rollback e mantém o fluxo mais fácil de auditar.
        plano: list[tuple[object, object, Decimal]] = []
        for componente in componentes:
            necessario = Decimal(str(componente.quantidade_prevista))
            restante = necessario
            lotes = self.repository.lotes_fefo_para_consumo(componente.produto_id)

            disponivel = sum(Decimal(str(lote.quantidade)) for lote in lotes)
            if disponivel < necessario:
                raise EstoqueInsuficiente(
                    f"estoque insuficiente para o produto {componente.produto_id}: "
                    f"necessário {float(necessario):g}, disponível {float(disponivel):g}"
                )

            for lote in lotes:
                if restante <= 0:
                    break
                saldo = Decimal(str(lote.quantidade))
                usar = min(restante, saldo)
                if usar > 0:
                    plano.append((componente, lote, usar))
                    restante -= usar

        movimentos: list[models.MovimentacaoEstoque] = []
        for componente, lote, usar in plano:
            movimentos.append(
                self.repository.inserir_movimentacao(
                    tipo="CONSUMO_PRODUCAO",
                    lote_origem=lote,
                    lote_destino=None,
                    quantidade=float(usar),
                    quantidade_destino=None,
                    motivo=None,
                    usuario_id=usuario_id,
                    solicitacao_producao_id=solicitacao_id,
                    sp_componente_id=componente.id,
                    observacao="Baixa automática FEFO da produção",
                )
            )

        return movimentos

    def registrar_perdas_producao(
        self, *, solicitacao_id: int, perdas: Iterable[tuple[object, object]], usuario_id: int
    ) -> list[models.MovimentacaoEstoque]:
        """Registra desperdícios adicionais da produção usando FEFO.

        Cada item de ``perdas`` contém o SpComponente já validado pela feature de
        produção e o schema com quantidade/motivo. A perda é retirada do estoque
        e permanece ligada ao componente para compor o realizado auditável.
        """
        self._validar_usuario_ativo(usuario_id)
        plano: list[tuple[object, object, Decimal, str]] = []
        for componente, perda in perdas:
            necessario = Decimal(str(perda.quantidade))
            restante = necessario
            lotes = self.repository.lotes_fefo_para_consumo(componente.produto_id)
            disponivel = sum(Decimal(str(lote.quantidade)) for lote in lotes)
            if disponivel < necessario:
                raise EstoqueInsuficiente(
                    f"estoque insuficiente para registrar perda do produto {componente.produto_id}: "
                    f"necessário {float(necessario):g}, disponível {float(disponivel):g}"
                )
            for lote in lotes:
                if restante <= 0:
                    break
                usar = min(restante, Decimal(str(lote.quantidade)))
                if usar > 0:
                    plano.append((componente, lote, usar, perda.motivo))
                    restante -= usar

        movimentos: list[models.MovimentacaoEstoque] = []
        for componente, lote, usar, motivo in plano:
            movimentos.append(
                self.repository.inserir_movimentacao(
                    tipo="PERDA",
                    lote_origem=lote,
                    lote_destino=None,
                    quantidade=float(usar),
                    quantidade_destino=None,
                    motivo=motivo,
                    usuario_id=usuario_id,
                    solicitacao_producao_id=solicitacao_id,
                    sp_componente_id=componente.id,
                    observacao="Perda registrada durante a produção",
                )
            )
        return movimentos

    def registrar_produto_final(
        self, *, solicitacao_id: int, produto_id: int, quantidade: Decimal,
        usuario_id: int, localizacao_destino_id: int | None = None
    ) -> models.MovimentacaoEstoque | None:
        """Cria o lote produzido e sua entrada sem abrir uma nova transação."""
        self._validar_usuario_ativo(usuario_id)
        if self.repository.existe_producao(solicitacao_id):
            raise Conflito("o produto final desta produção já foi registrado")
        if quantidade <= 0:
            return None

        produto = self.repository.obter_produto_ativo(produto_id)
        if produto is None:
            raise RegistroNaoEncontrado("produto final não encontrado ou inativo")
        if produto.tipo == "INSUMO":
            raise ValueError("um insumo não pode receber entrada de produção")

        if localizacao_destino_id is not None:
            localizacao = self.repository.obter_localizacao(localizacao_destino_id)
            if localizacao is None:
                raise RegistroNaoEncontrado("localização de destino não encontrada")
        else:
            localizacao = self.repository.primeira_localizacao_compativel(produto.conservacao)
            if localizacao is None:
                raise Conflito("não existe localização compatível para o produto produzido")

        self._validar_conservacao_compativel(produto, localizacao)
        hoje = date.today()
        validade = None
        if produto.validade_padrao_dias is not None:
            validade = hoje + timedelta(days=int(produto.validade_padrao_dias))
        elif produto.validade_obrigatoria:
            raise Conflito(
                "produto produzido exige validade, mas não possui validade_padrao_dias"
            )

        lote = self.repository.inserir_lote(
            produto_id=produto.id,
            localizacao_id=localizacao.id,
            data_producao=hoje.isoformat(),
            data_validade=validade.isoformat() if validade else None,
            custo_unitario=None,
        )
        return self.repository.inserir_movimentacao(
            tipo="PRODUCAO",
            lote_origem=lote,
            lote_destino=None,
            quantidade=float(quantidade),
            quantidade_destino=None,
            motivo=None,
            usuario_id=usuario_id,
            solicitacao_producao_id=solicitacao_id,
            sp_componente_id=None,
            observacao="Entrada automática do produto acabado",
        )

    def _obter_lote_ou_falhar(self, lote_id: int, para_atualizacao: bool = False):
        lote = self.repository.obter_lote(lote_id, para_atualizacao=para_atualizacao)
        if lote is None:
            raise ValueError(f"lote {lote_id} não encontrado")
        return lote

    def _validar_usuario_ativo(self, usuario_id: int) -> None:
        if self.repository.obter_usuario_ativo(usuario_id) is None:
            raise ValueError("usuário não encontrado ou inativo")

    def _validar_saldo_suficiente(self, origem, dados: schemas.MovimentacaoCreate) -> None:
        if dados.tipo in TIPOS_QUE_EXIGEM_SAIDA and origem.quantidade < dados.quantidade:
            raise ValueError("saldo insuficiente")

    def _validar_lote_destino(self, origem, dados: schemas.MovimentacaoCreate):
        destino = self._obter_lote_ou_falhar(dados.lote_destino_id, para_atualizacao=True)
        local_destino = self.repository.obter_localizacao(destino.localizacao_id)
        produto_destino = self.repository.obter_produto(destino.produto_id)
        if (
            not local_destino
            or not produto_destino
            or produto_destino.conservacao != local_destino.tipo
        ):
            raise ValueError("lote destino com conservação incompatível")
        if dados.tipo == "TRANSFERENCIA" and origem.produto_id != destino.produto_id:
            raise ValueError("transferência requer mesmo produto")
        if dados.tipo == "TRANSFORMACAO" and not self.repository.transformacao_cadastrada(
            origem.produto_id, destino.produto_id
        ):
            raise ValueError("transformação não cadastrada entre os produtos")
        return destino

    def listar_localizacoes(self) -> list[models.Localizacao]:
        return self.repository.listar_localizacoes()

    def criar_localizacao(self, dados: schemas.LocalizacaoCreate) -> models.Localizacao:
        with self.repository.transacao():
            if dados.parent_id is not None:
                self._validar_localizacao_existe(dados.parent_id)
            return self.repository.inserir_localizacao(
                nome=dados.nome, tipo=dados.tipo, parent_id=dados.parent_id, foto_url=dados.foto_url
            )

    def atualizar_localizacao(
        self, loc_id: int, dados: schemas.LocalizacaoUpdate
    ) -> models.Localizacao:
        campos = dados.model_dump(exclude_unset=True)

        with self.repository.transacao():
            self._obter_localizacao_ou_falhar(loc_id)
            if "parent_id" in campos and campos["parent_id"] is not None:
                self._validar_novo_parent(loc_id, campos["parent_id"])
            if "tipo" in campos and self.repository.existe_incompatibilidade_tipo(
                loc_id, campos["tipo"]
            ):
                raise ValueError(
                    "alteração de tipo incompatível com produtos dos lotes existentes"
                )
            self.repository.atualizar_localizacao(loc_id, campos)
            return self.repository.obter_localizacao(loc_id)

    def remover_localizacao(self, loc_id: int) -> None:
        with self.repository.transacao():
            if not self.repository.remover_localizacao(loc_id):
                raise RegistroNaoEncontrado("localização não encontrada")

    def _obter_localizacao_ou_falhar(self, loc_id: int):
        localizacao = self.repository.obter_localizacao(loc_id)
        if localizacao is None:
            raise RegistroNaoEncontrado("localização não encontrada")
        return localizacao

    def _validar_localizacao_existe(self, loc_id: int) -> None:
        if self.repository.obter_localizacao(loc_id) is None:
            raise ValueError("localização pai não encontrada")

    def _validar_novo_parent(self, loc_id: int, pai: int) -> None:
        self._validar_localizacao_existe(pai)
        if pai == loc_id:
            raise ValueError("localização não pode ser pai de si mesma")
        visitados = {loc_id}
        atual = pai
        while atual is not None:
            if atual in visitados:
                raise ValueError("hierarquia de localização circular")
            visitados.add(atual)
            atual = self.repository.obter_localizacao(atual).parent_id

    def listar_lotes(
        self, produto_id: Optional[int] = None, localizacao_id: Optional[int] = None
    ) -> list[models.LoteListado]:
        linhas = self.repository.listar_lotes(produto_id, localizacao_id)
        return [models.LoteListado.from_row(r) for r in linhas]

    def obter_lote(self, lote_id: int) -> Optional[models.Lote]:
        return self.repository.obter_lote(lote_id)

    def criar_lote(self, dados: schemas.LoteCreate) -> models.Lote:
        with self.repository.transacao():
            produto, localizacao = self._buscar_produto_e_localizacao(
                dados.produto_id, dados.localizacao_id
            )
            self._validar_validade_obrigatoria(
                produto, dados.data_validade, "produto exige data_validade"
            )
            self._validar_conservacao_compativel(produto, localizacao)
            return self.repository.inserir_lote(
                produto_id=dados.produto_id,
                localizacao_id=dados.localizacao_id,
                data_producao=dados.data_producao.isoformat() if dados.data_producao else None,
                data_validade=dados.data_validade.isoformat() if dados.data_validade else None,
                custo_unitario=dados.custo_unitario,
            )

    def atualizar_lote(self, lote_id: int, dados: schemas.LoteUpdate) -> models.Lote:
        campos = dados.model_dump(exclude_unset=True, mode="json")

        with self.repository.transacao():
            lote = self._obter_lote_ou_404(lote_id)
            validade = campos.get("data_validade", lote.data_validade)
            producao = campos.get("data_producao", lote.data_producao)
            produto = self.repository.obter_produto(lote.produto_id)
            self._validar_validade_obrigatoria(produto, validade, "validade obrigatória")
            if producao and validade and producao > validade:
                raise ValueError("validade anterior à produção")
            self.repository.atualizar_lote(lote_id, campos)
            return self.repository.obter_lote(lote_id)

    def _buscar_produto_e_localizacao(self, produto_id: int, localizacao_id: int):
        produto = self.repository.obter_produto_ativo(produto_id)
        localizacao = self.repository.obter_localizacao(localizacao_id)
        if not produto or not localizacao:
            raise ValueError("produto ativo ou localização não encontrados")
        return produto, localizacao

    def _validar_validade_obrigatoria(self, produto, data_validade, mensagem: str) -> None:
        if produto.validade_obrigatoria and not data_validade:
            raise ValueError(mensagem)

    def _validar_conservacao_compativel(self, produto, localizacao) -> None:
        if produto.conservacao != localizacao.tipo:
            raise ValueError("conservação do produto incompatível com localização")

    def _obter_lote_ou_404(self, lote_id: int):
        lote = self.repository.obter_lote(lote_id)
        if lote is None:
            raise RegistroNaoEncontrado("lote não encontrado")
        return lote
