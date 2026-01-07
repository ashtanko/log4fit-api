# Environment Configuration and Deployment Guide

This document provides instructions on how to configure and deploy the application for different environments.

## Configuration Strategy

The application uses a simple, environment-variable-driven approach. There are no complex configuration files. The application reads its settings directly from environment variables, with sensible defaults for local development.

- **Local Development**: When run without any special environment variables, the application defaults to a local development setup, using an in-memory H2 database.
- **Production**: When the `ENV` environment variable is set to `production`, the application switches to production mode, requiring a PostgreSQL database and JWT secrets.

## Environment Variables

### General
- `ENV`: The application environment. Set to `production` for the production environment. Defaults to `local`.
- `PORT`: The port the application will run on. Defaults to `8082` for local and `8083` for production.

### Production
These variables are **required** for the `production` environment.

- `DB_URL`: The JDBC URL for the PostgreSQL database (e.g., `jdbc:postgresql://your-prod-db-host:5432/prod_db`).
- `DB_USER`: The username for the database.
- `DB_PASSWORD`: The password for the database.
- `JWT_SECRET`: A strong, secret key for signing JWTs.
- `JWT_ISSUER`: The issuer for JWT tokens (e.g., `https://api.yourdomain.com`).
- `JWT_AUDIENCE`: The audience for JWT tokens (e.g., `your-app-name`).
- `JWT_REALM`: The realm for JWT authentication.

## Local Development

To run the application locally, simply run the `main` function in `Application.kt` from your IDE. No environment variables are needed. The application will start on port `8082` and use an in-memory H2 database.

## Deployment & Testing

The application is deployed using Docker and managed with a `Makefile`.

### Building the Docker Image
The image is built automatically when running the `make run-local` or `make run-prod` commands.

### Running with Docker Compose

- **Local Environment**:
  ```bash
  make run-local
  ```
  This command builds the Docker image and runs it on port `8082`, using the configuration from `docker-compose.local.yml` and `.env.local`.

- **Production Environment**:
  ```bash
  make run-prod
  ```
  This command builds the Docker image and runs it on port `8083`, using the configuration from `docker-compose.prod.yml` and `.env.prod`. Remember to fill in your production secrets in `.env.prod`.

### API Integration Testing
You can test the running application using the provided HTTP client tests.

- **Using IntelliJ HTTP Client**:
  Open `http-client.http` and run the requests. You can switch between `local` and `production` environments using the dropdown in the editor.

- **Using the Command Line (Docker)**:
  ```bash
  make test-api
  ```
  This command runs the `.http` tests against your local running instance.

- **Using Postman**:
  Import `postman_collection.json` and `postman_environment.json` into Postman to run the test suite.

## API Endpoints

### Authentication
- `POST /api/auth/register`: Register a new user.
- `POST /api/auth/login`: Log in and receive tokens.
- `POST /api/auth/refresh`: Refresh an access token using a refresh token.
- `GET /api/auth/me`: Get information about the currently authenticated user.

### Health Check
- `GET /health`: A simple health check endpoint.
