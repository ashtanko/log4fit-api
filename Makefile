.PHONY: run-local run-prod run-dev test-api docker-clean

run-local:
	docker compose -f docker-compose.local.yml up --build

run-prod:
	docker compose --env-file .env.prod -f docker-compose.yml -f docker-compose.prod.yml up --build

run-dev:
	docker compose --env-file .env.dev -f docker-compose.dev-db.yml -f docker-compose.dev.yml up --build

test-api:
	docker run --rm -it -v "$$(pwd):/workdir" -w /workdir --network host jetbrains/intellij-http-client -e local -v http-client.env.json http-client.http

docker-clean:
	docker compose -f docker-compose.yml -f docker-compose.prod.yml -f docker-compose.dev.yml down -v --remove-orphans
