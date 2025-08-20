# Spring Boot Challenge

The challenge is to develop some backend APIs.

## Prerequisite

* Java 17
* [MySQL](https://github.com/ytpor/mysql-challenge) version 8.0.41 or higher
* [Redis](https://github.com/ytpor/redis-challenge) version 7.4.1 or higher
* [RabbitMQ](https://github.com/ytpor/rabbitmq-challenge) version 4.1.1 or higher
* [MinIO](https://github.com/ytpor/minio-challenge)
* [OpenBao](https://github.com/ytpor/openbao-challenge)

## Get started

* Clone this repository
* Change directory into the newly cloned repository
* Make a copy of `.env.example`, and name it `.env`.
* Edit the content of `.env` with your environment in mind.

### Configuration

We need to set this up in `OpenBao`.

Create policy

    # Used when running ./gradlew bootRun
    echo 'path "secret/data/springboot-challenge" { capabilities = ["read"] }' | bao policy write springboot-challenge-policy -
    echo 'path "secret/data/springboot-challenge/dev" { capabilities = ["read"] }' | bao policy write springboot-challenge-dev-policy -
    # Used when running docker run
    echo 'path "secret/data/springboot-challenge/prod" { capabilities = ["read"] }' | bao policy write springboot-challenge-prod-policy -

Create AppRole

    bao write auth/approle/role/springboot-challenge-role token_policies="springboot-challenge-policy,springboot-challenge-dev-policy,springboot-challenge-prod-policy"

Fetch Role ID

    bao read auth/approle/role/springboot-challenge-role/role-id

Generate and fetch Secret ID

    bao write -f auth/approle/role/springboot-challenge-role/secret-id

Enable the Secrets Engine

    bao secrets enable -path=secret kv-v2

Add the secret

```
bao kv put secret/springboot-challenge/dev \
    jwt.key="M6tUuERk1fDWRCd2AJa2BzWTUQ+GPxzJbHlJEap16rXj72J4BeG6T1WiQcs8NCJ+" \
    minio.url="http://127.0.0.1:9000" \
    minio.access-key="username" \
    minio.secret-key="password" \
    rabbitmq.queue-category="queue_name" \
    rabbitmq.queue-category-key="queue_name" \
    rabbitmq.queue-item-attribute="other_queue_name" \
    rabbitmq.queue-item-attribute-key="other_queue_name" \
    rabbitmq.exchange-name="topic_exchange" \
    spring.data.redis.host="localhost" \
    spring.data.redis.port="6379" \
    spring.data.redis.password="password" \
    spring.datasource.url="jdbc:mysql://127.0.0.1:3306/database" \
    spring.datasource.username="username" \
    spring.datasource.password="password" \
    spring.rabbitmq.host="127.0.0.1" \
    spring.rabbitmq.port="5672" \
    spring.rabbitmq.username="username" \
    spring.rabbitmq.password="password" \
    spring.rabbitmq.virtual-host="virtual_host" \
    weatherapi.key="M6tUuERk1fDWRCd2AJa2BzWTUQ+GPxzJbHlJEap16rXj72J4BeG6T1WiQcs8NCJ+"
```

Verification

    bao kv get secret/springboot-challenge/dev

## Run the application

Navigate to the folder with `build.gradle`.

```
# Run
VAULT_URL=http://127.0.0.1:8200 \
VAULT_PATH=springboot-challenge \
VAULT_ROLE_ID=64b1f513-f6d6-df20-26b1-0d3e0f4d67f8 \
VAULT_SECRET_ID=fcf50e63-5b7c-49c4-1f46-b6af8df1f62f \
./gradlew bootRun
```

You can override the active profile with another, assuming that the profile exists, eg. `application-prod.properties`

```
VAULT_URL=http://127.0.0.1:8200 \
VAULT_PATH=springboot-challenge \
VAULT_ROLE_ID=64b1f513-f6d6-df20-26b1-0d3e0f4d67f8 \
VAULT_SECRET_ID=fcf50e63-5b7c-49c4-1f46-b6af8df1f62f \
./gradlew bootRun --args='--spring.profiles.active=prod'
```

You can then access the API documentation through the following URL:

```
http://localhost:8080/swagger-ui/index.html
```

## Run the tests

```
# Run test
./gradlew test

# Run specific test
./gradlew test --tests com.ytpor.api.controller.ItemAttributeControllerTest.testGetItemAttributeById
```

You can also find some sample HTTP requests in the [here](./rest-client).

## Building Docker Image

```
docker compose build

# -OR-
docker build -t springboot-challenge-app .
# -OR-
docker build --build-arg SPRING_PROFILE=prod -t springboot-challenge-app .
```

## Running the Docker Image

As our MySQL, Redis, RabbitMQ and MinIO runs in Docker, we can access services using their `container_name`.

```
# Update these value and create a new path. Take note of 'prod'
bao kv put secret/springboot-challenge/prod \
    ...
    minio.url="http://minio:9000" \
    spring.data.redis.host="redis" \
    spring.datasource.url="jdbc:mysql://mysql:3306/database" \
    spring.rabbitmq.host="rabbitmq" \
    ...
```

Run the docker image

```
# Relies on values from .env
docker compose up
```

Or,

```
docker run -e VAULT_URL=http://openbao-server:8200 \
    -e VAULT_PATH=springboot-challenge \
    -e VAULT_ROLE_ID=64b1f513-f6d6-df20-26b1-0d3e0f4d67f8 \
    -e VAULT_SECRET_ID=fcf50e63-5b7c-49c4-1f46-b6af8df1f62f \
    -e SPRING_PROFILES_ACTIVE=prod \
    --network nginx-proxy \
    -p 8080:8080 springboot-challenge-app
```