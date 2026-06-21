# Trabalho Qualidade e Teste - Grupo 2
Sistema de ERP web desenvolvido em Java com Spring Framework com testes automatizados

## Alunos:
- Lucas Sodré
- Italo Ferreira
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
- Instale Java JDK8 (Pode ser a versão Temurin JDK 8) e adicione ao seu JAVA_HOME 
- Com o VScode (ou algum terminal similar aberto), rode os seguintes comandos em sequência em um terminal:
    - ``docker-compose down``
    - ``mvn clean``
    - ``export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=256m"`` (Se não rodar, passe pro próximo)
    - ``mvn clean package -DskipTests``
    - ``docker-compose up --build`` (Aguarde até o app pdv iniciar)
- Abra um terminal adicional e rode:


# Rodar pós setup
- Testes unitarios - precisa dar build complete para rodar os testes a seguir.
    - ``mvn test -DforkCount=0`` 
- Teste de Mutação
    - ``mvn test-compile pitest:mutationCoverage -DforkCount=0`` 
- Teste Estrutural JoCoCo
    - ``mvn clean test jacoco:report`` 

# SonarCube
- Passo 1: Subir o servidor do SonarQube via DockerAbra o seu terminal (pode ser no VSCode mesmo) e execute o comando abaixo para baixar e iniciar o SonarQube na versão compatível com o seu Java: 
    - ``Bashdocker run -d --name sonarqube -p 9000:9000 sonarqube:lts-community``

- Passo 2: Acessar o painel e gerar o Token de acessoAbra o seu navegador e acesse: http://localhost:9000 
Faça o login com as credenciais padrão:Login: admin Senha: admin 
O sistema vai pedir para você trocar a senha imediatamente. Escolha uma nova senha (ex: admin123).
    - Após entrar, vá no canto superior direito (no ícone do seu perfil) e clique em My Account (Minha Conta).Vá na aba Security (Segurança).No campo "Generate Token", digite um nome qualquer (ex: meu-token-pdv), escolha o tipo "User Token" e clique em Generate.
    Copie o token gerado (uma sequência longa de letras e números). Você vai precisar dele no próximo passo.

- Passo 3: Preparar o relatório do JaCoCoO SonarQube não roda os testes de cobertura sozinho; ele lê o relatório XML que o JaCoCo gera.
Então, primeiro, gere os relatórios rodando o comando que ajustamos anteriormente (com as aspas para o PowerShell):  
    - ``mvn clean test jacoco:report``

- Passo 4: Enviar a análise para o SonarQubeCom os testes executados com sucesso e o relatório gerado, agora é só disparar o scanner do SonarQube através do Maven, passando o token que você copiou no Passo 2.Cole o comando abaixo no terminal, substituindo SEU_TOKEN_COPIADO pelo código real:
    - Bashmvn sonar: ``sonar "-Dsonar.projectKey=trabalho-pdv-grupo2" "-Dsonar.host.url=http://localhost:9000" "-Dsonar.login=SEU_TOKEN_COPIADO"``

- Passo 5: Ver o resultado!Assim que o comando no terminal terminar com a mensagem BUILD SUCCESS, volte para o seu navegador no http://localhost:9000.
O projeto "trabalho-pdv-grupo2" aparecerá na tela inicial! Ao clicar nele, você verá o painel completo de qualidade da sua classe ProdutoService, mostrando a porcentagem de cobertura (Coverage), os Code Smells, Bugs e Vulnerabilidades identificados pelo Sonar.

# Rodar para desativar o sistema. 
- ``docker-compose down``
- ``mvn clean``


# Login administrador
Para logar no sistema, use o usuário "gerente" e a senha "123".

# Tecnologias utilizadas
- Spring Framework 5
- Thymeleaf 3
- MySQL
- Hibernate
- FlyWay
- Docker