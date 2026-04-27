# Código fonte para testes do projeto PDV
Sistema de ERP web desenvolvido em Java com Spring Framework 

Alunos:
- Lucas Sodré
- Italo Ferreira
- Nicholas Lima
- Filipe dos Santos
- Caio Felipe

# Recursos
- Cadastro produtos/clientes/fornecedor
- Controle de estoque
- Gerenciar comandas
- Realizar venda
- Controle de fluxo de caixa
- Controle de pagar e receber
- Venda com cartões
- Gerenciar permissões de usuários por grupos
- Cadastrar novas formas de pagamentos
- Relatórios

# Setup
- Clone o projeto em uma pasta vazia
- Instale Maven no seu computador
- Com o VScode (ou algum terminal similar aberto), rode ``mvn clean package -DskipTests``
- Então, rode o comando ``docker compose up -d`` ou rode o arquivo ``docker-compose.yml``, na pasta raíz
- Finalmente, rode ``mvn test``

# Login administrador
Para logar no sistema, use o usuário "gerente" e a senha "123".

# Tecnologias utilizadas
- Spring Framework 5
- Thymeleaf 3
- MySQL
- Hibernate
- FlyWay
- Docker

