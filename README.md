# Log4Fit - Ktor Exercise Tracker Backend

[![Build Status](https://img.shields.io/github/actions/workflow/status/shtanko/log4fit/main.yml?branch=main)](https://github.com/shtanko/log4fit/actions)

Log4Fit is a backend application for an exercise tracking platform, built with Kotlin and Ktor. It provides a simple and robust foundation for logging workouts, tracking progress, and viewing statistics.

## Features

- **User Management**: Secure user registration and login with JWT-based authentication.
- **Exercise Tracking**: A predefined library of exercises and the ability for users to create their own.
- **Workout Logging**: Start/finish workout sessions and log sets with details like reps, weight, and rest time.
- **Statistics**: Basic progress tracking for workout volume and frequency.
- **Simple Configuration**: Environment-variable-driven setup for easy local development and production deployment.

## Tech Stack

- **Backend**: Kotlin, Ktor
- **Database**: PostgreSQL (Production), H2 (Local Development)
- **Authentication**: JWT
- **Build Tool**: Gradle
- **Containerization**: Docker & Docker Compose

## Getting Started

This project is fully containerized, so all you need is Docker installed.

### 1. Local Development (In-Memory Database)

This is the quickest way to get the application running. It uses an in-memory H2 database, so no external database is required.

```bash
make run-local
```

The application will be available at `http://localhost:8082`.

### 2. Development with a Real Database

To run the application with a real PostgreSQL database (recommended for more thorough testing), use the development database environment.

```bash
docker compose -f dev.docker-compose.yml up --build
```

This command starts:
- The **Ktor Application** on `http://localhost:8084`
- A **PostgreSQL** database on `localhost:5434`
- **PgAdmin** (database GUI) on `http://localhost:5054`
- **Dozzle** (log viewer) on `http://localhost:9999`

## API Testing

The project includes a suite of API integration tests using the `.http` file format.

### Running Tests from the Command Line

You can run the entire test suite against your local running instance with a single command:

```bash
make test-api
```

This uses the JetBrains HTTP Client CLI in a Docker container to execute the tests defined in `http-client.http`.

### Running Tests from the IDE

1.  Open the `http-client.http` file in IntelliJ IDEA or Android Studio.
2.  Select the `local` environment from the dropdown menu at the top.
3.  Click the "Run All Requests" button to execute the tests.

## API Endpoints

### Authentication
- `POST /api/auth/register`: Register a new user.
- `POST /api/auth/login`: Log in and receive tokens.
- `POST /api/auth/refresh`: Refresh an access token.
- `GET /api/auth/me`: Get information about the currently authenticated user.

### Health Check
- `GET /health`: A simple health check endpoint.
