# Deployment and Maintenance Guide

This document outlines the procedures for deploying, updating, and maintaining the Log4Fit application in a production environment.

## Prerequisites

Ensure the target server has the following installed:
- **Docker**: The container runtime.
- **Docker Compose**: For orchestrating the application services.
- **Git**: For pulling the latest code.

## Initial Deployment

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/shtanko/log4fit.git
    cd log4fit
    ```

2.  **Configure Environment**:
    Create a `.env.prod` file based on `example.env.prod`. **Crucially**, ensure you set a strong, unique password for `DB_PASSWORD` and `JWT_SECRET`.
    ```bash
    cp example.env.prod .env.prod
    nano .env.prod
    ```

3.  **Start the Application**:
    Use the provided Makefile command to start the production stack.
    ```bash
    make run-prod
    ```
    This will build the Docker image and start the application and database containers in detached mode.

## Updating the Application

To deploy a new version of the application without data loss:

1.  **Pull Latest Code**:
    ```bash
    git pull origin main
    ```

2.  **Rebuild and Restart**:
    Run the production command again. Docker Compose is smart enough to recreate only the containers that have changed (i.e., the application container with the new code), while leaving the database container (and its volume) intact.
    ```bash
    make run-prod
    ```

    *Note: This will cause a brief downtime while the application container restarts. For zero-downtime deployments, see the section below.*

3.  **Run Database Migrations**:
    If the update includes database schema changes, run the migrations. Since the production database is inside Docker, you can run the migration task against it.
    ```bash
    # Ensure you have the correct DB credentials exported or in your .env
    ./gradlew flywayMigrate
    ```

## Zero-Downtime Deployment Strategies

To avoid the brief downtime during a restart, you can use one of the following strategies.

### Strategy 1: Cloudflare Tunnel (Recommended)

Cloudflare Tunnel allows you to expose your application securely without opening ports on your firewall. It also provides load balancing capabilities that can be used for zero-downtime deployments.

1.  **Install `cloudflared`**: Install the Cloudflare daemon on your server.
2.  **Authenticate**: Run `cloudflared tunnel login`.
3.  **Create a Tunnel**: Run `cloudflared tunnel create log4fit-prod`.
4.  **Configure the Tunnel**: Create a `config.yml` file:
    ```yaml
    tunnel: <Tunnel-UUID>
    credentials-file: /root/.cloudflared/<Tunnel-UUID>.json

    ingress:
      - hostname: api.log4.fit
        service: http://localhost:8083
      - service: http_status:404
    ```
5.  **Run the Tunnel**: `cloudflared tunnel run log4fit-prod`.

**For Zero-Downtime Updates:**
Cloudflare's load balancing (available on paid plans or via manual DNS switching) allows you to run two instances of your application (Blue/Green) and switch traffic to the new one only after it's healthy.

### Strategy 2: Blue-Green Deployment with Nginx

This involves running two instances of your application (Blue and Green) behind a local Nginx reverse proxy.

1.  **Update Docker Compose**: Define two services (`app-blue` and `app-green`) pointing to the same image but different internal ports.
2.  **Configure Nginx**: Set up Nginx to proxy traffic to `http://app-blue:8080`.
3.  **Deploy New Version**:
    *   Pull code and build.
    *   Start the *inactive* container (e.g., Green) with the new code.
    *   Wait for it to become healthy.
    *   Update Nginx config to point to `http://app-green:8080`.
    *   Reload Nginx (`nginx -s reload`).
    *   Stop the old Blue container.

## Data Safety & Backups

**The most critical rule: Never delete the Docker volume `log4fit-prod_postgres_data`.** This volume contains your production database files.

### Backing Up the Database

You should automate regular backups of your PostgreSQL database. Here is a command to manually create a backup:

```bash
# Replace 'log4fit-prod-db-1' with your actual database container name
docker exec -t log4fit-prod-db-1 pg_dumpall -c -U your_db_user > dump_`date +%d-%m-%Y"_"%H_%M_%S`.sql
```

### Restoring from Backup

To restore data from a SQL dump:

```bash
cat your_dump.sql | docker exec -i log4fit-prod-db-1 psql -U your_db_user -d your_db_name
```

## Maintenance

-   **Logs**: Monitor application logs using `docker logs -f log4fit-prod-ktor-app-1` or set up a centralized logging solution like Loki/Grafana (as configured in the dev environment).
-   **Disk Space**: Periodically run `docker system prune` to remove unused images and build cache, but **be careful not to use the `-a` or `--volumes` flags unless you know what you are doing.**

## Troubleshooting

If the application fails to start after an update:
1.  Check the logs: `docker logs log4fit-prod-ktor-app-1`
2.  Verify environment variables in `.env.prod`.
3.  Ensure the database container is running: `docker ps`
