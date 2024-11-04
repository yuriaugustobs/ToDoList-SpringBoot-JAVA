FROM maven:3.8.5-openjdk-17 AS build

# Define o diretório de trabalho
WORKDIR /build

# Copia apenas o pom.xml primeiro
COPY pom.xml .

# Copia todo o resto do projeto
COPY . .

# Executa o build do projeto
RUN mvn clean package -DskipTests

# Segunda etapa - apenas para rodar a aplicação
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copia o arquivo JAR gerado
COPY --from=build /build/target/*.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"] 