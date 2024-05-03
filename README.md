# ElasticSearch Spring Boot Application

This is a Spring Boot application that connects to an Elasticsearch instance running in a Docker container. It provides
full-text search capabilities on the data stored in Elasticsearch.

## Prerequisites

- Java 21
- Docker
- Docker Compose

## Getting Started

1. Clone the repository:

```bash
git clone https://github.com/irocbaa1/elasticsearch-spring-boot.git
cd elasticsearch-spring-boot
```

2. Start Elasticsearch using Docker Compose:

```bash
docker-compose up -d
```

This will start Elasticsearch on localhost:9200. You can verify that Elasticsearch is running by
visiting http://localhost:9200 in your web browser or using a tool like curl:

```bash
curl http://localhost:9200
```

3. Run the Spring Boot application:

```bash
./gradlew bootRun
```

The application will start on localhost:8080. You can verify that the application is running by
visiting http://localhost:8080 in your web browser.

## Usage

The application provides a REST API for searching and indexing data in Elasticsearch. You can use tools like curl or
Postman to interact
with the API.

- POST /v1/employee: Add a new employee.
- GET /v1/employee/{id}: Retrieve an employee by ID.
- GET /v1/employees: Retrieve all employees.
- DELETE /v1/employee/{id}: Delete an employee by ID.
- GET /v1/employees/count: Count the number of employees.
- GET /v1/employees/search: Search for employees by firstName/lastName/city/position
- POST /v1/employees/init: Generate a set of employees.

## Full Text Search

The application uses Elasticsearch's full-text search capabilities to search for employees by first name. This is done
through the /v1/employees/search endpoint. You can specify the query parameter to search for a specific name:

```bash
curl http://localhost:8080/v1/employees/search?query=John
```

This will return a list of employees whose first name contains "John".
