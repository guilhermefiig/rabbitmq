# Prática RabbitMQ

Projeto de estudo de mensageria com RabbitMQ e Spring Boot, composto por duas APIs:

- **producer** — recebe requisições HTTP e publica mensagens na fila.
- **consumer** — escuta a fila e processa as mensagens, com retry e fila de erro.

```
[cliente HTTP] --> producer :8081 --> RabbitMQ (MY_TEST_QUEUE) --> consumer :8080
                                              |
                                              +--> boq.MY_TEST_QUEUE (mensagens que falharam)
```

## Stack

- Java 21
- Spring Boot 4.1 (Spring AMQP, Spring Web MVC)
- Jackson 3
- springdoc-openapi (Swagger UI) no producer
- RabbitMQ 3 (management) via Docker Compose

## Estrutura

```
pratica-rabbitMQ/
├── docker-compose.yml   # RabbitMQ
├── producer/            # API que publica mensagens
└── consumer/            # API que consome mensagens
```

## Como rodar

### 1. Subir o RabbitMQ

Na raiz do projeto:

```bash
docker compose up -d
```

- AMQP: `localhost:5672`
- Painel de administração: http://localhost:15672 (usuário `myuser`, senha `mypassword`)

### 2. Subir o consumer

```bash
cd consumer
./mvnw spring-boot:run
```

O consumer declara as filas `MY_TEST_QUEUE` e `boq.MY_TEST_QUEUE` ao iniciar.

### 3. Subir o producer

```bash
cd producer
./mvnw spring-boot:run
```

> É necessário Java 21. Se o `JAVA_HOME` apontar para outra versão, o build falha com `class file has wrong version`.

## Testando

Com o producer rodando, acesse o Swagger:

http://localhost:8081/swagger-ui/index.html

### Endpoints do producer

| Método | Rota                 | Descrição                                   |
|--------|----------------------|---------------------------------------------|
| POST   | `/producer/messages` | Publica o corpo da requisição como texto    |
| POST   | `/producer/json`     | Publica o corpo como JSON (`QueueMessage`)  |

Exemplo de corpo para `/producer/json`:

```json
{
  "key1": "valor 1",
  "key2": "valor 2"
}
```

Exemplo com curl:

```bash
curl -X POST http://localhost:8081/producer/json \
  -H "Content-Type: application/json" \
  -d '{"key1": "valor 1", "key2": "valor 2"}'
```

O consumer imprime a mensagem recebida no console.

## Retry e fila de erro (consumer)

Se o processamento de uma mensagem falhar, o consumer:

1. Tenta novamente até 2 vezes, com backoff exponencial (1s, 2s, até no máximo 5s).
2. Esgotadas as tentativas, republica a mensagem na fila `boq.MY_TEST_QUEUE`.

## Configuração

As duas APIs leem a conexão do `application.properties`:

| Propriedade          | Valor padrão    |
|----------------------|-----------------|
| `rabbitmq.host`      | `localhost`     |
| `rabbitmq.port`      | `5672`          |
| `rabbitmq.username`  | `myuser`        |
| `rabbitmq.password`  | `mypassword`    |
| `rabbitmq.queuename` | `MY_TEST_QUEUE` |

> As credenciais são apenas para ambiente local de estudo.
