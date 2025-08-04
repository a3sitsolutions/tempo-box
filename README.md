# Tempo Box - Sistema de Compartilhamento Temporário de Arquivos

Sistema web para upload e compartilhamento temporário de arquivos com controle de expiração automática. Desenvolvido em Spring Boot com interface web responsiva e API REST para integração.

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

- Java 17+
- PostgreSQL
- Gradle

### Configuração do Banco de Dados

1. Crie um banco PostgreSQL:
```sql
CREATE DATABASE tempo_box;
CREATE USER admin WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE tempo_box TO admin;
```

2. Configure o `application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/tempo_box
spring.datasource.username=admin
spring.datasource.password=admin

# Application Configuration
app.file.upload-dir=./uploads
app.auth.static-token=tempo-box-admin-token-2024
app.cleanup.interval=3600000
```

### Executando a Aplicação

```bash
# Clone o repositório
git clone <repository-url>
cd tempo-box

# Execute a aplicação
./gradlew bootRun
```

A aplicação estará disponível em `http://localhost:8080`

## 🔗 API Endpoints

### Upload de Arquivo
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@arquivo.txt" \
  -F "storageDurationMinutes=60" \
  -F "authToken=tempo-box-admin-token-2024" \
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
# Acesso direto autenticado
curl -L http://localhost:8080/auth/tempo-box-admin-token-2024
```

## 🌐 Interface Web

### Login
- **URL:** `http://localhost:8080/login`
- **Token:** `tempo-box-admin-token-2024`

### Autenticação Automática
- **URL:** `http://localhost:8080/auth/tempo-box-admin-token-2024`
- Redireciona automaticamente para a área de arquivos

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

## 🚦 Status e Monitoramento

### Logs
A aplicação registra todas as operações importantes:
- Upload e download de arquivos
- Autenticação e sessões
- Limpeza automática
- Erros e exceções

### Health Check
- **URL:** `http://localhost:8080/actuator/health` (se Actuator estiver habilitado)

## 📝 Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/com/a3sitsolutions/tempo_box/
│   │   ├── config/          # Configurações (Security)
│   │   ├── controller/      # Controllers REST e Web
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # Entidades JPA
│   │   ├── filter/         # Filtros de segurança
│   │   ├── repository/     # Repositórios JPA
│   │   └── service/        # Serviços de negócio
│   └── resources/
│       ├── templates/      # Templates Thymeleaf
│       └── static/         # Recursos estáticos
└── test/                   # Testes unitários
```

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