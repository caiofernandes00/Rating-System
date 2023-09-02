docker_up_app:
	docker compose up --build

docker_up_deps:
	docker compose up postgres -d
	docker compose up kafka-topics-generator -d
	docker compose up control-center -d
	docker compose up schema-registry -d