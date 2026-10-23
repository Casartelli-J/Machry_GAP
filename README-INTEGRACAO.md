# MACHRY — Aplicação com login e controle de acesso

## Perfis

- **Cozinheiro:** Visão geral, Pedidos, Receitas, Estoque e Cadastros API.
- **Financeiro:** Visão geral, Vendas, Financeiro e Relatórios.
- **Gerente:** acesso operacional ampliado, incluindo compras, equipe e cadastros.
- **Administrador geral:** todos os módulos e gerenciamento de usuários.

O back-end valida o token e o perfil nos endpoints protegidos. Ocultar itens no menu não é a única camada de proteção.

## Requisitos

- Java 25, conforme o `pom.xml`.
- MySQL e phpMyAdmin (por exemplo, via XAMPP).
- Python para servir o front-end localmente.

## Configurar o banco

Crie o banco `machry_database` no MySQL. Se já usa o XAMPP, inicie o MySQL e confirme que o banco existe no phpMyAdmin; por padrão, a API tenta `localhost:3306`, usuário `root` e senha vazia.

Como alternativa, na pasta `Backend`, execute `docker compose up -d` para iniciar MySQL e phpMyAdmin. Nesse caso, abra o phpMyAdmin em `http://localhost:8081`, entre com usuário `root` e senha `machryRoot123`, e inicie a API com a variável `DB_PASSWORD=machryRoot123` definida no terminal. A API aceita as variáveis `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` para personalizar a conexão.

## Executar o back-end

No terminal, abra a pasta `Backend` e execute no Windows:

```bat
mvnw.cmd spring-boot:run
```

A API ficará em `http://localhost:8080`. O Hibernate cria/atualiza a tabela `usuarios` e as tabelas mapeadas pelas entidades.

## Primeiro acesso

Na primeira execução, a API cria um administrador se ainda não existir: `admin@machry.local` com senha `Machry@123`. Para definir credenciais iniciais diferentes, configure `MACHRY_ADMIN_EMAIL` e `MACHRY_ADMIN_PASSWORD` antes da primeira inicialização da API. A tela de alteração de senha ainda não faz parte desta versão.

## Executar o front-end

Na pasta `Frontend/Machry`, execute:

```bash
python -m http.server 5500
```

Abra `http://localhost:5500`. Entre com o administrador inicial e use a seção **Equipe** para criar usuários, definir perfis e ativar/desativar contas.

## Observações de segurança

- As senhas são armazenadas com BCrypt.
- Os tokens expiram após oito horas e ficam em memória; reiniciar a API encerra as sessões ativas.
- A senha padrão é apenas para desenvolvimento; não use essas credenciais em um ambiente real.
- Os módulos de financeiro/vendas existentes no front são demonstrativos; endpoints reais para esses módulos ainda precisam ser desenvolvidos no back-end.
