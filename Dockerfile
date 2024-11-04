FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copia os arquivos do Maven primeiro
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .

# Baixa as dependências
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src/ src/

# Executa o build
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/todolist-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 