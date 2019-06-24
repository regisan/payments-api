# payments-api

Projeto que simula processamento de transações de cartões de crédito.

## Requisitos para compilação e build
- Java 1.8 ou posterior
- Gradle 4.4
- Docker

## Comandos
Todos os comandos abaixo devem ser executados no diretorio raiz do projeto

### Build
`./gradlew build`

### Gerando bootJar
`./gradlew bootJar`

### Criando o container
`sudo docker-compose build`

### Subindo o container
`sudo docker-compose up`

### Encerrando o container
`sudo docker-compose down`

## Documentação da API
http://localhost/payments/api/swagger-ui.html

## Acesso ao banco de dados (H2 memory)
http://localhost/payments/api/h2-console/

- Driver Class: org.h2.Driver
- JDBC URL: jdbc:h2:mem:testdb
- User name: sa
- Password:

