# 📚 Tempo Box - Documentação da API

## 🌐 URLs de Acesso

### Produção
- **Aplicação**: https://tempo-box.a3sitsolutions.com.br
- **Swagger UI**: https://tempo-box.a3sitsolutions.com.br/swagger-ui.html
- **API Docs (JSON)**: https://tempo-box.a3sitsolutions.com.br/v3/api-docs
- **Health Check**: https://tempo-box.a3sitsolutions.com.br/actuator/health

### Desenvolvimento
- **Aplicação**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/v3/api-docs

## 📖 Visão Geral

O **Tempo Box** é uma API REST para upload e compartilhamento temporário de arquivos com controle de expiração automática. Ideal para compartilhamento de builds, artefatos CI/CD, documentos temporários e muito mais.

### 🎯 Principais Funcionalidades

- 📁 **Upload de arquivos** com duração personalizada (até 100MB)
- 🔒 **Autenticação por token** estático para segurança
- 👤 **Filtragem por ID Token** para organização de arquivos por usuário/contexto
- ⏰ **Auto-expiração** baseada em tempo configurável
- 🗂️ **Metadados customizáveis** (repositório, commit, branch, etc.)
- 🔄 **Limpeza automática** de arquivos expirados
- 🌐 **Interface web** para gerenciamento manual
- 📊 **API completa** para integração automática

## 🔐 Autenticação

### Auth Token (Obrigatório)
Todas as operações de upload requerem um **Auth Token** válido:
```
authToken: "tempo-box-admin-token-2024"
```

### ID Token (Opcional)
Para filtrar arquivos por usuário/contexto específico:
```
idToken: "token-identification-xpto"
```

### Access Token
Para download de arquivos (gerado automaticamente ou customizado):
```
accessToken: "custom-access-token-123"
```

## 🛠️ Endpoints da API

### 📤 Upload de Arquivo

**POST** `/api/files/upload`

Faz upload de um arquivo com tempo de vida configurável.

#### Parâmetros (Form Data)

| Parâmetro | Tipo | Obrigatório | Descrição | Exemplo |
|-----------|------|-------------|-----------|---------|
| `file` | File | ✅ | Arquivo a ser enviado (máx 100MB) | `arquivo.zip` |
| `storageDurationMinutes` | Integer | ✅ | Tempo de vida em minutos | `1440` |
| `authToken` | String | ✅ | Token de autenticação | `tempo-box-admin-token-2024` |
| `idToken` | String | ❌ | Token de identificação único | `token-identification-xpto` |
| `accessToken` | String | ❌ | Token personalizado para download | `custom-token-123` |
| `repository` | String | ❌ | Nome do repositório Git | `meu-projeto` |
| `commit` | String | ❌ | Hash do commit Git | `abc1234567890` |
| `commitMessage` | String | ❌ | Mensagem do commit | `feat: nova funcionalidade` |
| `branch` | String | ❌ | Nome da branch Git | `main` |
| `runId` | String | ❌ | ID da execução CI/CD | `12345` |
| `actor` | String | ❌ | Autor do commit/execução | `developer@example.com` |
| `apkName` | String | ❌ | Nome do APK (Android) | `app-release.apk` |
| `downloadUrl` | String | ❌ | URL alternativa de download | `https://example.com/file` |

#### Exemplo de Requisição

```bash
curl -X POST 'https://tempo-box.a3sitsolutions.com.br/api/files/upload' \
  -F 'file=@"arquivo.zip"' \
  -F 'authToken="tempo-box-admin-token-2024"' \
  -F 'idToken="token-identification-xpto"' \
  -F 'storageDurationMinutes="1440"' \
  -F 'repository="meu-projeto"' \
  -F 'branch="main"' \
  -F 'commit="abc123"'
```

#### Resposta de Sucesso (200)

```json
{
  "fileId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "accessToken": "custom-access-token-123",
  "message": "File uploaded successfully",
  "expiresInMinutes": 1440
}
```

#### Possíveis Erros

| Código | Descrição | Exemplo |
|--------|-----------|---------|
| 401 | Token inválido | `{"error": "Unauthorized", "message": "Invalid authentication token"}` |
| 500 | Erro no upload | `{"error": "Upload failed", "message": "IO exception occurred"}` |

---

### 📥 Download de Arquivo

**GET** `/api/files/download/{fileId}`

Faz download de um arquivo específico.

#### Parâmetros

| Parâmetro | Localização | Obrigatório | Descrição | Exemplo |
|-----------|-------------|-------------|-----------|---------|
| `fileId` | Path | ✅ | ID único do arquivo | `a1b2c3d4-e5f6-7890-abcd-ef1234567890` |
| `Access-Token` | Header | ✅ | Token de acesso | `custom-access-token-123` |

#### Exemplo de Requisição

```bash
curl -X GET 'https://tempo-box.a3sitsolutions.com.br/api/files/download/a1b2c3d4-e5f6-7890-abcd-ef1234567890' \
  -H 'Access-Token: custom-access-token-123' \
  -o arquivo_baixado.zip
```

#### Resposta de Sucesso (200)
Retorna o arquivo binário para download.

#### Possíveis Erros

| Código | Descrição | Exemplo |
|--------|-----------|---------|
| 401 | Token/arquivo inválido | `{"error": "Unauthorized", "message": "Invalid file ID or access token"}` |
| 404 | Arquivo não encontrado | `{"error": "File Not Found", "message": "File could not be found on server"}` |
| 410 | Arquivo expirado | `{"error": "File Expired", "message": "File has expired", "expiredAt": "2024-01-15T10:30:00"}` |

---

## 🌐 Interface Web

### Login com ID Token

A interface web agora suporta login com ID Token para filtrar arquivos:

1. **Auth Token**: Token de autenticação principal
2. **ID Token**: Token para filtrar apenas seus arquivos (opcional)

### URLs da Interface

- **Homepage**: `/`
- **Login**: `/login` 
- **Área de arquivos**: `/files`
- **Logout**: `/logout`
- **Login direto (Auth Token)**: `/auth/{authToken}`
- **Login direto (Auth + ID Token)**: `/auth/{authToken}/{idToken}`

---

### 🔗 Login Automático via URL

Para facilitar a integração e acesso direto, você pode usar URLs que fazem login automaticamente:

#### Login apenas com Auth Token
```
https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024
```
- Faz login automático
- Mostra todos os arquivos do Auth Token

#### Login com Auth Token + ID Token
```
https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024/token-identification-xpto
```
- Faz login automático
- Filtra apenas arquivos do ID Token especificado
- Ideal para contextos específicos (usuário, projeto, CI/CD)

#### Exemplos Práticos

**Para usuários específicos:**
```bash
# Usuário João
https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024/user-joao

# Usuário Maria  
https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024/user-maria
```

**Para projetos específicos:**
```bash
# Projeto A
https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024/project-a

# Projeto B
https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024/project-b
```

**Para builds CI/CD:**
```bash
# Build de produção
https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024/production-builds

# Build de desenvolvimento
https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024/dev-builds
```

---

## 🔧 Integração com CI/CD

### GitHub Actions

Exemplo de uso em workflow do GitHub Actions:

```yaml
- name: Upload Build Artifact
  run: |
    curl -X POST 'https://tempo-box.a3sitsolutions.com.br/api/files/upload' \
      -F 'file=@"build/app.zip"' \
      -F 'authToken="${{ secrets.TEMPO_BOX_TOKEN }}"' \
      -F 'idToken="github-${{ github.actor }}"' \
      -F 'storageDurationMinutes="2880"' \
      -F 'repository="${{ github.repository }}"' \
      -F 'branch="${{ github.ref_name }}"' \
      -F 'commit="${{ github.sha }}"' \
      -F 'commitMessage="${{ github.event.head_commit.message }}"' \
      -F 'actor="${{ github.actor }}"' \
      -F 'runId="${{ github.run_id }}"'
```

### Jenkins

```groovy
pipeline {
    agent any
    stages {
        stage('Upload Artifact') {
            steps {
                sh '''
                    curl -X POST 'https://tempo-box.a3sitsolutions.com.br/api/files/upload' \
                      -F 'file=@"dist/app.jar"' \
                      -F 'authToken="${TEMPO_BOX_TOKEN}"' \
                      -F 'idToken="jenkins-${BUILD_USER}"' \
                      -F 'storageDurationMinutes="1440"' \
                      -F 'repository="${JOB_NAME}"' \
                      -F 'branch="${GIT_BRANCH}"' \
                      -F 'commit="${GIT_COMMIT}"' \
                      -F 'runId="${BUILD_NUMBER}"'
                '''
            }
        }
    }
}
```

---

## 📊 Casos de Uso

### 1. Compartilhamento de Builds
```bash
# Upload de build de aplicação
curl -X POST 'https://tempo-box.a3sitsolutions.com.br/api/files/upload' \
  -F 'file=@"app-v1.2.3.apk"' \
  -F 'authToken="tempo-box-admin-token-2024"' \
  -F 'idToken="android-builds"' \
  -F 'storageDurationMinutes="4320"' \
  -F 'apkName="app-v1.2.3.apk"'
```

### 2. Artefatos de CI/CD
```bash
# Upload de artefato com metadados completos
curl -X POST 'https://tempo-box.a3sitsolutions.com.br/api/files/upload' \
  -F 'file=@"release.zip"' \
  -F 'authToken="tempo-box-admin-token-2024"' \
  -F 'idToken="ci-cd-releases"' \
  -F 'storageDurationMinutes="10080"' \
  -F 'repository="my-app"' \
  -F 'branch="release/v1.2.3"' \
  -F 'commit="abc123def456"' \
  -F 'commitMessage="release: version 1.2.3"'
```

### 3. Documentos Temporários
```bash
# Upload de documento com vida curta
curl -X POST 'https://tempo-box.a3sitsolutions.com.br/api/files/upload' \
  -F 'file=@"relatorio.pdf"' \
  -F 'authToken="tempo-box-admin-token-2024"' \
  -F 'idToken="documentos-temporarios"' \
  -F 'storageDurationMinutes="60"'
```

---

## 🛡️ Segurança

### Tokens de Segurança
- **Auth Token**: Controla acesso ao upload
- **Access Token**: Controla acesso ao download
- **ID Token**: Organiza e filtra arquivos por contexto

### Headers de Segurança
- Frame Deny
- Content Type NoSniff  
- XSS Filter
- Referrer Policy

### Limitações
- Tamanho máximo: 100MB por arquivo
- Tempo mínimo: 1 minuto
- Tempo máximo: Configurável (padrão sem limite)

---

## ⚡ Performance

### Cache
- Cache de layers Docker para builds
- Cache de dependências Gradle
- Headers de cache HTTP

### Limpeza Automática
- Execução a cada hora (configurável)
- Remoção de arquivos expirados
- Limpeza de registros do banco

---

## 🔍 Monitoramento

### Health Checks
- **Aplicação**: `/actuator/health`
- **Informações**: `/actuator/info`

### Logs
- Upload e download de arquivos
- Autenticação e sessões
- Limpeza automática
- Erros e exceções

---

## 🐳 Deploy com Docker

### Variáveis de Ambiente

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/tempo_box
  - SPRING_DATASOURCE_USERNAME=admin
  - SPRING_DATASOURCE_PASSWORD=admin
  - APP_AUTH_STATIC_TOKEN=tempo-box-admin-token-2024
  - APP_VERSION=1.0.0
```

### Docker Compose

```yaml
version: '3.8'
services:
  tempo-box:
    image: a3s.nexus.maranguape.a3sitsolutions.com.br/tempo-box:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    labels:
      - "traefik.http.routers.tempo-box.rule=Host(`tempo-box.a3sitsolutions.com.br`)"
```

---

## 🚀 Exemplos Práticos

### Upload com JavaScript

```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('authToken', 'tempo-box-admin-token-2024');
formData.append('idToken', 'my-unique-id');
formData.append('storageDurationMinutes', '1440');

fetch('https://tempo-box.a3sitsolutions.com.br/api/files/upload', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => {
    console.log('Upload successful:', data);
    // data.fileId e data.accessToken para download
});
```

### Download com Python

```python
import requests

headers = {
    'Access-Token': 'custom-access-token-123'
}

response = requests.get(
    'https://tempo-box.a3sitsolutions.com.br/api/files/download/file-id-here',
    headers=headers
)

if response.status_code == 200:
    with open('downloaded_file.zip', 'wb') as f:
        f.write(response.content)
    print('Download successful!')
```

---

## 📞 Suporte

### Contato
- **Email**: contato@a3sitsolutions.com.br
- **Website**: https://a3sitsolutions.com.br

### Issues
- **GitHub**: Reporte problemas no repositório oficial

### Documentação
- **Swagger UI**: Interface interativa completa
- **README.md**: Documentação de instalação e configuração
- **API_DOCUMENTATION.md**: Este documento

---

**🎉 Tempo Box - Compartilhamento temporário de arquivos de forma simples e segura!**