# Tempo Box - Sistema de Compartilhamento Temporário de Arquivos

Sistema web para upload e compartilhamento temporário de arquivos com controle de expiração automática. Desenvolvido em Spring Boot com interface web responsiva e API REST para integração.

> 📚 **[Documentação Completa da API](API_DOCUMENTATION.md)** - Guia detalhado com exemplos práticos, casos de uso e integração CI/CD

## 🚀 Principais Funcionalidades

- **Upload de arquivos** com duração personalizada (minutos/horas/dias)
- **Interface web** para gerenciamento de arquivos
- **API REST** para automação e integração
- **Autenticação por token** estático
- **Auto-expiração** e limpeza automática de arquivos
- **Expiração manual** via interface web
- **Metadados customizáveis** (repository, commit, branch, etc.)
- **Suporte a arquivos** até 100MB

## 🔧 Tecnologias

- **Backend:** Java 17+ / Spring Boot 3.x
- **Segurança:** Spring Security
- **Banco de Dados:** PostgreSQL / Spring Data JPA / Hibernate
- **Frontend:** Thymeleaf / Bootstrap 5 / Font Awesome
- **Recursos:** Scheduled Tasks / File Management

## 📦 Casos de Uso

- Compartilhamento temporário de builds/artefatos
- Transferência segura de arquivos sensíveis
- Integração com pipelines CI/CD
- Distribuição temporária de documentos
- Cache de arquivos com TTL automático

## 🛠️ Configuração e Instalação

### Pré-requisitos

- **Para desenvolvimento local:** Java 17+, PostgreSQL, Gradle
- **Para Docker:** Docker e Docker Compose

### Configuração Local (Desenvolvimento)

1. Crie um banco PostgreSQL:
```sql
CREATE DATABASE tempo_box;
CREATE USER admin WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE tempo_box TO admin;
```

2. Configure o `application.properties` (já configurado):
```properties
# Database Configuration (Development - Local)
spring.datasource.url=jdbc:postgresql://192.168.1.112:5432/tempo_box
spring.datasource.username=admin
spring.datasource.password=admin

# Application Configuration
app.file.upload-dir=./uploads
app.auth.static-token=tempo-box-admin-token-2024
app.cleanup.interval=3600000
```

### Profiles de Configuração

O projeto possui dois profiles:

- **Default** (`application.properties`): Para desenvolvimento local
- **Docker** (`application-docker.properties`): Para containers Docker

O profile `docker` é ativado automaticamente no Docker Compose via `SPRING_PROFILES_ACTIVE=docker`.

### Executando a Aplicação

```bash
# Clone o repositório
git clone <repository-url>
cd tempo-box

# Execute a aplicação
./gradlew bootRun
```

A aplicação estará disponível em `http://localhost:8080`

## 🐳 Deploy com Docker

### Configuração de Variáveis de Ambiente

A aplicação utiliza variáveis de ambiente para configuração segura:

```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar variáveis conforme necessário
nano .env
```

**Principais variáveis:**
- `AUTH_TOKEN`: Token de autenticação (configurado via secrets na pipeline)
- `APP_VERSION`: Versão da aplicação  
- `CLEANUP_INTERVAL`: Intervalo de limpeza automática

### Usando Docker Hub/Nexus

A aplicação está disponível como imagem Docker no repositório Nexus:

```bash
# Login no repositório Nexus
docker login a3s.nexus.maranguape.a3sitsolutions.com.br

# Pull da imagem
docker pull a3s.nexus.maranguape.a3sitsolutions.com.br/tempo-box:latest
```

### Deploy com Docker Compose (Recomendado)

O Docker Compose está configurado para comunicação interna entre os serviços, **sem expor a porta do PostgreSQL** externamente.

```bash
# Clone o repositório
git clone <repository-url>
cd tempo-box

# Configurar variáveis de ambiente (opcional)
cp .env.example .env
# Editar .env com suas configurações personalizadas

# Inicie os serviços (PostgreSQL + Tempo Box)
docker-compose up -d

# Ou com variáveis específicas
AUTH_TOKEN=meu-token-secreto docker-compose up -d

# Visualizar logs
docker-compose logs -f

# Parar os serviços
docker-compose down

# Visualizar status dos serviços
docker-compose ps
```

**Características da configuração:**
- ✅ PostgreSQL **não expõe porta externa** (apenas rede interna)
- ✅ Comunicação via nome do serviço: `tempo-box-postgres:5432`
- ✅ Health checks para garantir ordem de inicialização
- ✅ Profile `docker` ativado automaticamente
- ✅ Volumes persistentes para dados e uploads

### Deploy Manual com Docker

```bash
# Execute apenas a aplicação (requer PostgreSQL externo)
docker run -d \
  --name tempo-box \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://seu-postgres-host:5432/tempo_box \
  -e SPRING_DATASOURCE_USERNAME=admin \
  -e SPRING_DATASOURCE_PASSWORD=admin \
  -e APP_AUTH_STATIC_TOKEN=tempo-box-admin-token-2024 \
  -v $(pwd)/uploads:/app/uploads \
  a3s.nexus.maranguape.a3sitsolutions.com.br/tempo-box:latest
```

### Deploy com PostgreSQL Externo

```bash
# Para conectar com PostgreSQL externo (não Docker)
docker run -d \
  --name tempo-box \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.1.112:5432/tempo_box \
  -e SPRING_DATASOURCE_USERNAME=admin \
  -e SPRING_DATASOURCE_PASSWORD=admin \
  -e APP_AUTH_STATIC_TOKEN=tempo-box-admin-token-2024 \
  -v $(pwd)/uploads:/app/uploads \
  a3s.nexus.maranguape.a3sitsolutions.com.br/tempo-box:latest
```

## 📚 Documentação da API

### Swagger UI (Recomendado)
- **Produção**: https://tempo-box.a3sitsolutions.com.br/swagger-ui.html 
- **Local**: http://localhost:8080/swagger-ui.html

### OpenAPI Specification
- **Produção**: https://tempo-box.a3sitsolutions.com.br/v3/api-docs
- **Local**: http://localhost:8080/v3/api-docs

### Documentação Completa
- **Arquivo**: [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Guia completo da API
  - 📖 Visão geral e funcionalidades
  - 🔐 Sistema de autenticação com tokens
  - 📤 Upload e download detalhados
  - 🔗 Login automático via URL
  - 🔧 Integração CI/CD (GitHub Actions, Jenkins)
  - 📊 Casos de uso práticos
  - 🚀 Exemplos de código (JavaScript, Python, cURL)
  - 🐳 Deploy e configuração Docker

## 🔗 API Endpoints

> 💡 **Para exemplos detalhados e casos de uso completos, consulte a [Documentação da API](API_DOCUMENTATION.md)**

### Upload de Arquivo
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@arquivo.txt" \
  -F "storageDurationMinutes=60" \
  -F "authToken=tempo-box-admin-token-2024" \
  -F "idToken=token-identification-xpto" \
  -F "accessToken=meu-token-personalizado"
```

### Download de Arquivo
```bash
curl -X GET http://localhost:8080/api/files/download/{fileId} \
  -H "Access-Token: meu-token-personalizado" \
  -o arquivo_baixado.txt
```

### Autenticação Automática
```bash
# Acesso direto apenas com Auth Token
curl -L http://localhost:8080/auth/tempo-box-admin-token-2024

# Acesso direto com Auth Token e ID Token
curl -L http://localhost:8080/auth/tempo-box-admin-token-2024/token-identification-xpto
```

## 🌐 Interface Web

### Login
- **URL:** `http://localhost:8080/login`
- **Token:** `tempo-box-admin-token-2024`

### Autenticação Automática
- **Apenas Auth Token:** `http://localhost:8080/auth/tempo-box-admin-token-2024`
- **Com ID Token:** `http://localhost:8080/auth/tempo-box-admin-token-2024/token-identification-xpto`
- Redireciona automaticamente para a área de arquivos com filtros aplicados

### Gerenciamento de Arquivos
- **URL:** `http://localhost:8080/files`
- Listagem, download e expiração manual de arquivos
- Visualização de metadados e informações detalhadas

## 📊 Funcionalidades da Interface

- **Listagem de arquivos** com status de expiração
- **Visualização de tamanho** inteligente (B, KB, MB, GB)
- **Informações detalhadas** em modal
- **Download direto** via interface
- **Expiração manual** com confirmação
- **Responsive design** para mobile

## ⚙️ Configurações Avançadas

### Limpeza Automática
A aplicação executa limpeza automática de arquivos expirados a cada hora (configurável via `app.cleanup.interval`).

### Segurança
- Autenticação baseada em token estático
- Validação de sessão para interface web
- Headers de segurança configurados
- CSRF desabilitado para API REST

### Upload de Arquivos
- Tamanho máximo: 100MB
- Armazenamento local em `./uploads`
- Metadados opcionais para integração CI/CD

## 🚀 CI/CD Pipeline

### GitHub Actions

O projeto inclui pipeline automatizada para build e deploy:

**Arquivos da Pipeline:**
- `.github/workflows/deploy.yml` - Pipeline principal
- `Dockerfile` - Configuração da imagem Docker
- `docker-compose.yml` - Deploy completo com PostgreSQL

**Fluxo da Pipeline (5 Jobs Separados):**

1. **🧪 Test Job** - Execução de testes unitários
   - Cache Gradle otimizado
   - Testes paralelos com build cache
   - Upload de relatórios de teste

2. **🏗️ Build Job** - Build da aplicação 
   - Reutiliza cache do job anterior
   - Build paralelo otimizado
   - Upload de artefatos (.jar)

3. **🐳 Docker Job** - Build e Push da imagem
   - Docker Buildx com cache de layers
   - Cache incremental entre builds
   - Push para Nexus Registry

4. **🧹 Cleanup Job** - Limpeza automática
   - Remove imagens locais
   - Libera espaço em disco
   - Docker system prune

5. **📊 Summary Job** - Relatório final
   - Status de todos os jobs
   - URLs das imagens publicadas
   - Resumo da execução

**Triggers Disponíveis:**
- 🔄 **Push/PR:** Execução automática
- 🚀 **Manual:** Botão "Run workflow" com opções
- ⚡ **Deploy Only:** Pula testes (modo rápido)

**Imagens Geradas:**
```
a3s.nexus.maranguape.a3sitsolutions.com.br/tempo-box:latest
a3s.nexus.maranguape.a3sitsolutions.com.br/tempo-box:<commit-sha>
```

### Secrets Necessários
- `NEXUS_REPOSITORY`: a3s.nexus.maranguape.a3sitsolutions.com.br  
- `NEXUS_PORT`: 8082
- `NEXUS_USER`: admin  
- `NEXUS_PASSWORD`: ******** 
- `AUTH_TOKEN`: ******** (Token de autenticação da aplicação)

### Otimizações de Performance
- **🚀 Cache Gradle:** Dependências e wrapper cacheados
- **🐳 Docker Buildx:** Cache de layers entre builds
- **⚡ Build Paralelo:** `--parallel --build-cache`
- **📦 Artefatos:** Reutilização entre jobs
- **🔄 Jobs Condicionais:** Execução baseada em sucesso/falha

### Execução Manual de Jobs
```bash
# Executar apenas testes
gh workflow run deploy.yml

# Deploy rápido (pular testes)  
gh workflow run deploy.yml -f deploy_only=true

# Via interface GitHub
# Actions → 🚀 Build and Deploy to Nexus → Run workflow
```

## 🚦 Status e Monitoramento

### Logs
A aplicação registra todas as operações importantes:
- Upload e download de arquivos
- Autenticação e sessões
- Limpeza automática
- Erros e exceções

### Docker Logs
```bash
# Logs da aplicação
docker-compose logs -f app

# Logs do PostgreSQL
docker-compose logs -f postgres

# Logs de todos os serviços
docker-compose logs -f
```

### Health Check
- **Simples:** `http://localhost:8080/health` - Health check rápido e confiável
- **Detalhado:** `http://localhost:8080/health/detailed` - Com informações adicionais
- **Actuator:** `http://localhost:8080/actuator/health` - Health check do Spring Boot

## 📝 Estrutura do Projeto

```
tempo-box/
├── .github/
│   └── workflows/
│       └── deploy.yml          # Pipeline CI/CD
├── src/
│   ├── main/
│   │   ├── java/br/com/a3sitsolutions/tempo_box/
│   │   │   ├── config/         # Configurações (Security)
│   │   │   ├── controller/     # Controllers REST e Web
│   │   │   ├── dto/           # Data Transfer Objects
│   │   │   ├── entity/        # Entidades JPA
│   │   │   ├── filter/        # Filtros de segurança
│   │   │   ├── repository/    # Repositórios JPA
│   │   │   └── service/       # Serviços de negócio
│   │   └── resources/
│   │       ├── templates/     # Templates Thymeleaf
│   │       └── static/        # Recursos estáticos
│   └── test/                  # Testes unitários
├── Dockerfile                 # Configuração Docker
├── docker-compose.yml         # Deploy completo
├── .dockerignore             # Exclusões Docker
├── build.gradle              # Configuração Gradle
└── README.md                 # Documentação
```

# 🚀 Deploy Tempo Box no Portainer com Traefik

## 📋 Pré-requisitos

### 1. Criar diretórios no host:
```bash
sudo mkdir -p /opt/docker-data/tempo-box/{postgres,uploads,backups}
sudo chown -R 1000:1000 /opt/docker-data/tempo-box/
sudo chmod -R 755 /opt/docker-data/tempo-box/
```

### 2. Configurar rede Traefik:
```bash
# Se não existir, criar a rede a3snet
docker network create --driver overlay --attachable a3snet
```

### 3. Login no Nexus Registry (no manager node):
```bash
docker login a3s.nexus.maranguape.a3sitsolutions.com.br
```

## 🎯 Deploy no Portainer

### 1. **Acessar Portainer**
- URL: `https://portainer.a3sitsolutions.com.br`
- Login com suas credenciais

### 2. **Criar Nova Stack**
- Ir em **Stacks** → **Add stack**
- **Name**: `tempo-box`
- **Build method**: Web editor

### 3. **Colar o docker-compose.portainer.yml**
- Copiar todo o conteúdo do arquivo `docker-compose.portainer.yml`
- Colar no editor do Portainer

### 4. **Configurar Environment Variables**
**Obrigatório:** Definir o AUTH_TOKEN como variável de ambiente:
```env
AUTH_TOKEN=seu-token-secreto-seguro
APP_VERSION=1.0.0
CLEANUP_INTERVAL=3600000
```

**Opcionais para personalização:**
```env
POSTGRES_PASSWORD=SuaSenhaSegura
JAVA_OPTS=-Xmx2048m -Xms1024m
```

> 💡 **Importante**: O `AUTH_TOKEN` deve ser definido nas variáveis de ambiente da stack no Portainer para segurança.

### 5. **Deploy da Stack**
- Clicar em **Deploy the stack**
- Aguardar todos os serviços ficarem running

## 🌐 URLs de Acesso

Após o deploy bem-sucedido:

### **Aplicação Principal:**
- 🚀 **Tempo Box**: https://tempo-box.a3sitsolutions.com.br
- 🔐 **Login direto**: https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024

### **Funcionalidades:**
- 📁 **Upload de arquivos**: Interface web completa
- 🔗 **API REST**: Para integração e automação
- ⏰ **Auto-expiração**: Limpeza automática de arquivos
- 🗂️ **Gestão manual**: Expirar arquivos via interface

## 📊 Monitoramento

### **Logs no Portainer:**
1. Ir em **Stacks** → **tempo-box**
2. Clicar no serviço desejado
3. Aba **Logs** para ver saídas

### **Health Checks:**
- ✅ **PostgreSQL**: `pg_isready` interno
- ✅ **Aplicação**: Endpoint `/health` via wget (mais rápido e confiável)
- ✅ **Traefik**: Health check automático no endpoint `/health`

### **Serviços incluídos:**
- 🗄️ **tempo_box_postgres**: Banco de dados
- 🚀 **tempo_box_app**: Aplicação principal
- 💾 **tempo_box_backup**: Backup automático diário
- 📊 **tempo_box_monitor**: Monitoramento a cada 5min

## 🔧 Configurações Especiais

### **Recursos alocados:**
- **PostgreSQL**: 1 CPU, 512MB RAM
- **Aplicação**: 2 CPU, 1.5GB RAM
- **Backup**: 0.5 CPU, 256MB RAM
- **Monitor**: 0.1 CPU, 64MB RAM

### **Volumes persistentes:**
- **Database**: `/opt/docker-data/tempo-box/postgres`
- **Uploads**: `/opt/docker-data/tempo-box/uploads`
- **Backups**: `/opt/docker-data/tempo-box/backups`

### **Backup automático:**
- ⏰ **Frequência**: Diário (24h)
- 📦 **Inclui**: Banco de dados + arquivos
- 🧹 **Retenção**: 7 dias
- 📍 **Local**: `/opt/docker-data/tempo-box/backups`

### **Security Headers (Traefik):**
- ✅ Frame Deny
- ✅ Content Type NoSniff
- ✅ XSS Filter
- ✅ Referrer Policy

## 🚨 Troubleshooting

> 📖 **[Guia Completo de Troubleshooting](TROUBLESHOOTING.md)** - Soluções detalhadas para problemas comuns

### **Se a aplicação não subir:**
```bash
# Verificar logs
docker service logs tempo-box_tempo_box_app

# Verificar se o Nexus está acessível
docker pull a3s.nexus.maranguape.a3sitsolutions.com.br:8082/tempo-box:latest
```

### **Se o banco não conectar:**
```bash
# Verificar PostgreSQL
docker service logs tempo-box_tempo_box_postgres

# Testar conexão manual
docker exec -it <container_id> psql -U admin -d tempo_box
```

### **Se o Traefik não rotear:**
- Verificar se o domínio `tempo-box.a3sitsolutions.com.br` aponta para o servidor
- Confirmar se a rede `a3snet` existe e está configurada
- Verificar logs do Traefik para certificado SSL

## ✅ Checklist pós-deploy

- [ ] Aplicação acessível via HTTPS
- [ ] Login funciona com token
- [ ] Upload de arquivo funciona
- [ ] Download de arquivo funciona
- [ ] Auto-expiração funcionando
- [ ] Backup rodando diariamente
- [ ] Logs sem erros críticos
- [ ] SSL/TLS válido (Let's Encrypt)

## 🔄 Atualizações

Para atualizar a aplicação:
1. Nova imagem no Nexus via pipeline
2. No Portainer: **Stacks** → **tempo-box** → **Editor**
3. Clicar em **Update the stack**
4. Portainer fará o rolling update automaticamente

---

**🎉 Tempo Box está pronto para produção!**
Acesse: https://tempo-box.a3sitsolutions.com.br

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👥 Autores

- **A3S IT Solutions** - *Desenvolvimento inicial*

---

**Tempo Box** - Compartilhamento temporário de arquivos de forma simples e segura! 🚀