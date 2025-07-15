# Spring Boot Challenge

The challenge is to develop some backend APIs.

## Prerequisite

* Java 17
* MySQL version 8.0.41 or higher
* Redis version 7.4.1 or higher
* RabbitMQ version 4.1.1 or higher
* MinIO

## Get started

* Clone this repository
* Change directory into the newly cloned repository
* Make a copy of `.env.example`, and name it `.env`.
* Edit the content of `.env` with your environment in mind.

**NOTE** Clone [simple-jasypt](https://github.com/ytpor/simple-jasypt), and use it to encrypt the MySQL and Redis passwords, and store the encrypted string to `SBC_MYSQL_DB_PASSWORD` and `SBC_REDIS_PASSWORD`.

## Run the application

Navigate to the folder with `build.gradle`.

```
# Run
./gradlew bootRun
```

```
# Build and run
./gradlew build
java -jar ./build/libs/springboot-challenge-0.0.1-SNAPSHOT.jar
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
