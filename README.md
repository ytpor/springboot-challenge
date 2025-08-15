# Spring Boot Challenge

The challenge is to develop some backend APIs.

## Prerequisite

* Java 17
* [MySQL](https://github.com/ytpor/mysql-challenge) version 8.0.41 or higher
* [Redis](https://github.com/ytpor/redis-challenge) version 7.4.1 or higher
* [RabbitMQ](https://github.com/ytpor/rabbitmq-challenge) version 4.1.1 or higher
* [MinIO](https://github.com/ytpor/minio-challenge)

## Get started

* Clone this repository
* Change directory into the newly cloned repository
* Make a copy of `.env.example`, and name it `.env`.
* Edit the content of `.env` with your environment in mind.

## Run the application

Navigate to the folder with `build.gradle`.

```
# Run
./gradlew bootRun
```

You can override the active profile with another, assuming that the profile exists, eg. `application-docker.properties`

```
./gradlew bootRun --args='--spring.profiles.active=docker'
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
```

## Running the Docker Image

As our MySQL, Redis, RabbitMQ and MinIO runs in Docker, we can access services using that their `container_name`.

```
# .env
SBC_MYSQL_DB_URL=jdbc:mysql://mysql:3306/member_db
SBC_MINIO_URL=http://minio:9000
SBC_RABBITMQ_HOST=rabbitmq
SBC_REDIS_HOST=redis
```

```
# Using docker compose
docker compose up -d
```

Get environment value from a file.

```
# Run command in directory with the .env file
docker run -p 8080:8080 --env-file .env springboot-challenge-app
```
