FROM openjdk:17-jdk-slim

# Build arguments
ARG AUTH_TOKEN
ARG APP_VERSION=1.0.0
ARG CLEANUP_INTERVAL=3600000

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

# Configurar variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker
ENV AUTH_TOKEN=${AUTH_TOKEN}
ENV APP_VERSION=${APP_VERSION}
ENV CLEANUP_INTERVAL=${CLEANUP_INTERVAL}

# Executar aplicação com profile docker
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]