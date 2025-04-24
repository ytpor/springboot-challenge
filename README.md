# Spring Boot Challenge

The challenge is to develop some backend APIs.

## Prerequisite

* Java 17

## Get started

Set the environment variables. Run from command line.

```
export SBC_MYSQL_DB_URL=jdbc:mysql://127.0.0.1:3306/challenge_db
export SBC_MYSQL_DB_USERNAME=username
export SBC_MYSQL_DB_PASSWORD=password
```

## Run the application

Navigate to the folder with `build.gradle`.

```
# Run
./gradlew bootRun
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
