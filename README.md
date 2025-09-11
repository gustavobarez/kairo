# Kairo - Scheduling System

![JAVA_BADGE](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![SPRING_BADGE](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![AWS_BADGE](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![DOCKER_COMPOSE](https://img.shields.io/badge/Docker%20Compose-%231d63ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![TERRAFORM](https://img.shields.io/badge/Terraform-%23623ce4.svg?style=for-the-badge&logo=terraform&logoColor=white)

This project is an appointment scheduling API, built with Java 21 and Spring Boot 3.5. The API allows users to create accounts, manage their profiles, and organize their appointments efficiently.

---

## 🚀 Features

- User Management: Specialized endpoints for creating, updating, and deleting user accounts.
- Appointment System: Create, update, delete, and list appointments with support for invitations and participants.
- Secure Authentication: JWT-based login system for route protection and sensitive data security.
- Health Monitoring: Application health endpoints and statistics for system monitoring.
- API Documentation: Interactive API exploration and documentation provided by Swagger UI (OpenAPI).
- Containerization: Ready to deploy using Docker, with a multi-stage Dockerfile for optimized images.

## 🏗️ Installation

To use this project, you need to follow these steps:

1. Clone the repository: `git clone https://github.com/gustavobarez/b3-portfolio-manager.git`
2. Install the dependencies: `mvn clean package`
3. Run the application: `mvn spring-boot:run`

## ⚙️ Makefile Commands

The project includes a Makefile to help you manage common tasks more easily. Here's a list of the available commands and a brief description of what they do:

- `make run`: Run the application locally
- `make build`: Build the application and package a JAR
- `make test`: Run tests for all packages in the project.
- `make docs`: Generate Swagger API documentation
- `make docker-build`: Build the Docker image for the application
- `make docker-run`: Run the application in a Docker container
- `make clean`: Clean project build artifacts

To use these commands, simply type `make` followed by the desired command in your terminal. For example:

```sh
make run
```

## 🐳 Docker and Docker Compose

This project includes a `Dockerfile` and `docker-compose.yml` file for easy containerization and deployment. Here are the most common Docker and Docker Compose commands you may want to use:

- `docker build -t your-image-name .`: Build a Docker image for the project. Replace `your-image-name` with a name for your image.
- `docker run -p 8080:8080 -e PORT=8080 your-image-name`: Run a container based on the built image. Replace `your-image-name` with the name you used when building the image. You can change the port number if necessary.

If you want to use Docker Compose, follow these commands:

- `docker compose build`: Build the services defined in the `docker-compose.yml` file.
- `docker compose up`: Run the services defined in the `docker-compose.yml` file.

To stop and remove containers, networks, and volumes defined in the `docker-compose.yml` file, run:

```sh
docker-compose down
```

For more information on Docker and Docker Compose, refer to the official documentation:

- [Docker](https://docs.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## 🛠️ Used Tools

This project uses the following tools:

- [Java](https://docs.oracle.com/en/java/javase/21/) for backend development
- [Spring Boot](https://docs.spring.io/spring-boot/index.html) framework for building APIs
- [Docker](https://docs.docker.com/) for containerization
- [Terraform](https://developer.hashicorp.com/terraform/docs) for IAC
- [Swagger](https://swagger.io/) for API documentation and testing

## 💻 Usage

After the API is running, you can use the Swagger UI to interact with the available endpoints for user and appointments. The API can be accessed at `http://localhost:$PORT/swagger/index.html`.

Default $PORT if not provided=8080.

## 🤝 Contributing

To contribute to this project, please follow these guidelines:

1. Fork the repository
2. Create a new branch: `git checkout -b feature/your-feature-name`
3. Make your changes and commit them using Conventional Commits
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE.md file for details.

## ❤️ Credits

This project was created by [Gustavo Barez](https://github.com/gustavobarez).