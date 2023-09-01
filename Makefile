docker_up_app:
	docker compose up --build

docker_up_deps:
	docker compose up postgres
	docker compose up kafka-topics-generator
	docker compose up control-center
	docker compose up schema-registry