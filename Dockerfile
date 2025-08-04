FROM openjdk:17-jdk-slim

# Instalar wget para health checks
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Criar diretório da aplicação
WORKDIR /app

# Copiar arquivos de build
COPY build/libs/*.jar app.jar

# Criar diretório para uploads
RUN mkdir -p /app/uploads

# Expor porta
EXPOSE 8080

# Configurar JVM para container e profile Docker
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

# Executar aplicação com profile docker
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]