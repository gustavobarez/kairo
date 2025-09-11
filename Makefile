.PHONY: default run build test docs docker-run docker-build deploy clean

default: run

run:
	mvn spring-boot:run

build:
	mvn clean package -DskipTests

test:
	mvn test

docs:
	mvn springdoc-openapi:generate

docker-build:
	docker build -t myapp .

docker-run:
	docker run -p 8080:8080 myapp

deploy:
	@echo "Deploy placeholder"

clean:
	mvn clean