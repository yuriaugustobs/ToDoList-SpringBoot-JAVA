# Define a imagem base como Ubuntu na versão mais recente
# O 'as build' define um estágio de construção nomeado que pode ser referenciado depois
FROM ubuntu:latest as build 

# Atualiza a lista de pacotes disponíveis no sistema
RUN apt-get update

# Instala o Java Development Kit (JDK) versão 17
# A flag -y responde 'yes' automaticamente para prompts de instalação
RUN apt-get install -y openjdk-17-jdk

# Copia todos os arquivos do diretório atual para o diretório raiz do container
COPY . .

# Instala o Maven
RUN apt-get install -y maven

# Executa o comando Maven para construir o projeto, pulando os testes
RUN mvn package -DskipTests

# Limpa o diretório de destino
RUN mvn clean install

# Exponha a porta 8080 para acesso externo
EXPOSE 8080

# Copia o arquivo JAR do estágio de construção para o diretório de destino
COPY --from=build /target/todolist-1.0.0.jar /app.jar

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "/app.jar"]

