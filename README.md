# To Do List

Uma aplicação simples que eu desenvolvi como parte de um projeto maior de uma trilha de desenvolvimento Java.

## Tecnologias Usadas

- **Java 17**
- **Spring Boot**
- **PostgreSQL**
- **Docker**
- **Gradle**
- **Testes Unitários**

## Como Rodar a Aplicação

### Requisitos

- Docker (para rodar a aplicação e o banco de dados)
- Java 17 (se preferir rodar localmente sem Docker)

### Rodando a aplicação com Docker

1. Clone este repositório:

    ```bash
    git clone https://github.com/lfernandops1/todolist
    cd todolist
    ```

2. Execute os containers:

    ```bash
    docker-compose up --build
    ```

3. A aplicação estará disponível em `http://localhost:8080`.

4. O banco de dados PostgreSQL estará disponível em `localhost:5432` (com usuário `seu_usuario` e senha `sua_senha`).

### Rodando a aplicação localmente

1. Faça o download do PostgreSQL localmente.
2. Configure o banco de dados `todolist` no PostgreSQL.
3. Altere as configurações de conexão no arquivo `application.properties` para refletir sua configuração local.
4. Execute a aplicação com Gradle ou no IntelliJ IDEA.

    ```bash
    ./gradlew bootRun
    ```

## Testando a API

Você pode testar a API enviando requisições para os endpoints definidos. Se preferir, posso exportar a coleção de requisições para o [Postman](https://www.postman.com) e incluir no repositório para facilitar os testes.

### Exemplos de Requisições

- **Cadastrar Atividade**:
    - **Método**: POST
    - **Endpoint**: `/api/atividade/criar`
    - **Body**:

      ```json
      {
          "titulo": "Entrada Titulo",
          "descricao": "Entrada Descrição",
          "completado": false
      }
      ```
- **Atualizar Atividade**:
    - **Método**: PUT
    - **Endpoint**: `/api/atividade/{uuid}`
    - **Body**:

      ```json
      {
          "titulo": "Att Titulo",
          "completado": true
      }
      ```

- **Listar Atividades**:
    - **Método**: GET
    - **Endpoint**: `/api/atividade`

- **Listar Por Id da Atividade**:
    - **Método**: GET
    - **Endpoint**: `/api/atividade/{uuid}`
    
- **Listar usando Filtros**:
    - **Método**: GET
    - **Endpoint**: `/api/atividade/pesquisar?titulo=Estudar&descricao=Entrada&completado=false`

- **Deletar Atividade**:
    - **Método**: DELETE
    - **Endpoint**: `/api/atividade/{uuid}`

## Configuração do Banco de Dados

O projeto usa PostgreSQL. As configurações de conexão estão no arquivo `application.properties` (ou `application.yml`) do Spring Boot.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/todolist
spring.datasource.username=seu_username
spring.datasource.password=sua_senha
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update --MODIFICAVEL
