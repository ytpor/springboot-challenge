# Spring Boot Challenge

The challenge is to develop some backend APIs.

## Prerequisite

* Java 17
* MySQL version 8.0.41 or higher
* Redis version 7.4.1 or higher

## Get started

Create and store configuration in `/etc/profile.d` so that it will be executed when a new shell session starts.

```
cd /etc/profile.d
sudo touch springboot_challenge.sh
sudo chmod +x springboot_challenge.sh
```

The content of `springboot_challenge.sh` would look like this. Change the values according to your environment.

```
# MySQL
export SBC_MYSQL_DB_URL=jdbc:mysql://127.0.0.1:3306/challenge_db
export SBC_MYSQL_DB_USERNAME=username
export SBC_MYSQL_DB_PASSWORD=password
# Redis
export SBC_REDIS_HOST=localhost
export SBC_REDIS_PORT=6379
export SBC_REDIS_PASSWORD=password # Remove line if Redis has no password
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
