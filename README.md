# Task Management API

A secure REST API for collaborative task management built with Spring Boot and JWT authentication.

## Features

- JWT Authentication and Authorization
- User registration and login
- Task creation and assignment
- Role-based access control (USER and ADMIN roles)
- Task status tracking and updates
- Comprehensive API documentation with Swagger

## Technologies Used

- Java 11
- Spring Boot 2.7.x
- Spring Security with JWT
- Spring Data JPA
- H2 Database (can be replaced with MySQL)
- Maven for dependency management
- JUnit for testing
- Swagger/OpenAPI for documentation

## API Endpoints

### Authentication

- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/signin` - Authenticate and get JWT token

### Tasks

- `GET /api/tasks` - Get all tasks (ADMIN) or assigned tasks (USER)
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create a new task
- `PATCH /api/tasks/{id}/status` - Update task status (only by assigned user)

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:

```
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Accessing API Documentation

Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

### Accessing H2 Console

The H2 console is available at:

```
http://localhost:8080/h2-console
```

Use the following credentials:
- JDBC URL: `jdbc:h2:mem:taskdb`
- Username: `sa`
- Password: `password`

## Testing

Run the tests using Maven:

```
mvn test
```

## CI/CD

This project includes a GitHub Actions workflow for continuous integration. On each push to the main branch, it will:

1. Build the project
2. Run all tests

The workflow configuration is in `.github/workflows/maven.yml`.