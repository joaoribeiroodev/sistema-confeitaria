<div align="center">

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=700&size=28&pause=1000&color=F97316&center=true&vCenter=true&width=600&lines=%F0%9F%8E%82+Sistema+Confeitaria;Fullstack+com+Spring+Boot+%2B+Angular;Gest%C3%A3o+de+Pedidos+%26+Produ%C3%A7%C3%A3o" alt="Typing SVG" />

<br/>
<br/>

**Plataforma fullstack para gerenciamento de encomendas de uma confeitaria artesanal.**
Vitrine pública para pedidos · Painel administrativo · Controle de produção · Relatórios gerenciais.

<br/>


## 🛠️ Stack

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Apache POI](https://img.shields.io/badge/Apache_POI-D22128?style=for-the-badge&logo=apache&logoColor=white)

![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-06B6D4?style=for-the-badge&logo=tailwind-css&logoColor=white)
![RxJS](https://img.shields.io/badge/RxJS-B7178C?style=for-the-badge&logo=reactivex&logoColor=white)
![PWA](https://img.shields.io/badge/PWA-5A0FC8?style=for-the-badge&logo=pwa&logoColor=white)

![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

</div>

---
<br/>

![Build](https://img.shields.io/badge/build-passing-brightgreen?style=flat-square&logo=github-actions)
![Version](https://img.shields.io/badge/version-v1.0.0-informational?style=flat-square)
![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)
![Status](https://img.shields.io/badge/status-active-success?style=flat-square)

</div>

---

## 🧭 Visão Geral

O **Sistema Confeitaria** resolve o problema de gestão manual de encomendas artesanais, digitalizando todo o fluxo — do pedido do cliente até a entrega.

```
┌─────────────────────────────────────────────────────────────────┐
│                        SISTEMA CONFEITARIA                      │
├──────────────────────────┬──────────────────────────────────────┤
│   🛍️  VITRINE PÚBLICA     │      🔧  PAINEL ADMINISTRATIVO       │
│   Angular 21 · PWA       │      Angular 21 · Route Guard        │
│                          │                                      │
│  · Catálogo de produtos  │  · Fila de pedidos em tempo real     │
│  · Carrinho de compras   │  · Atualização de status             │
│  · Agendamento de data   │  · Exportação de relatórios .xlsx    │
│  · Validação de limite   │  · Controle de capacidade diária     │
└──────────────┬───────────┴──────────────────┬───────────────────┘
               │                              │
               ▼          REST API            ▼
┌─────────────────────────────────────────────────────────────────┐
│                  ☕  BACKEND — Spring Boot 3.2.5                 │
│                                                                 │
│   Spring Security · JWT Auth · Spring Data JPA · Apache POI    │
│   Springdoc OpenAPI (Swagger) · Limite de pedidos por dia       │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
               ┌───────────────────────────┐
               │  🗄️  MySQL 8.0  (Docker)  │
               └───────────────────────────┘
```

---

## ⚡ Stack Tecnológica

<details open>
<summary><strong>☕ Backend</strong></summary>

<br/>

| Tecnologia | Versão | Função |
|---|---|---|
| **Java** | 21 LTS | Linguagem principal — records, sealed classes, pattern matching |
| **Spring Boot** | 3.2.5 | Framework da API REST, auto-configuração e IoC |
| **Spring Security** | 6.x | Autenticação, autorização e filtros por rota |
| **Spring Data JPA** | 3.x | Abstração de repositórios e queries sobre o banco |
| **JWT (JJWT)** | 0.11.5 | Tokens stateless para autenticação sem sessão |
| **Apache POI** | 5.2.5 | Geração de relatórios gerenciais em `.xlsx` (Excel) |
| **Springdoc OpenAPI** | 2.5.0 | Documentação interativa via Swagger UI |
| **Lombok** | — | Redução de boilerplate com anotações |
| **Maven** | 3.x | Build e gerenciamento de dependências |

</details>

<details open>
<summary><strong>🅰️ Frontend</strong></summary>

<br/>

| Tecnologia | Versão | Função |
|---|---|---|
| **Angular** | 21 | Framework SPA com Signals e componentes standalone |
| **TypeScript** | 5.9 | Tipagem estática, interfaces e decorators |
| **Tailwind CSS** | 4.x | Estilização utility-first e design responsivo |
| **RxJS** | 7.8 | Programação reativa: Observables, pipes e operadores |
| **Angular Router** | 21 | Navegação SPA com `canActivate` Guard |
| **Angular Service Worker** | 21 | PWA — app instalável com cache offline |

</details>

<details open>
<summary><strong>🐳 Infraestrutura</strong></summary>

<br/>

| Tecnologia | Função |
|---|---|
| **Docker** | Containerização da API, banco e frontend |
| **Docker Compose** | Orquestração completa dos 3 serviços com rede interna |
| **Nginx** | Serve o build Angular e faz proxy reverso em produção |
| **MySQL 8.0** | Banco relacional com timezone configurado para `America/Sao_Paulo` |
| **H2 Database** | Banco em memória para testes automatizados |
| **GitHub Actions** | CI/CD para validação e build automatizados |

</details>

---

## ✨ Funcionalidades

### 🛍️ Experiência do Cliente
- Catálogo de produtos com seleção e carrinho dinâmico
- Agendamento de data de retirada com validação em tempo real
- Verificação automática de limite de pedidos por dia
- Confirmação de pedido com feedback visual
- **Instalável como app** no celular via PWA

### 🔧 Painel Administrativo
- Login seguro com **JWT** — sessão protegida por `AdminGuard` no Angular
- Fila de pedidos do dia com atualização de status de produção
- Dashboard com métricas e histórico de pedidos
- **Exportação de relatórios** mensais em `.xlsx` via Apache POI

### 🔒 Segurança
- Autenticação **stateless com JWT** — sem sessão no servidor
- `JwtFilter` no backend + `HttpInterceptor` no frontend para injeção automática do token
- Rotas administrativas protegidas em dois níveis: frontend (Guard) e backend (Security)
- Swagger desabilitado automaticamente no perfil de produção

---

## 🗺️ Rotas da Aplicação

### Frontend (Angular)

| Rota | Componente | Acesso |
|---|---|---|
| `/` | `OrderComponent` | 🌐 Público — vitrine de pedidos |
| `/admin/login` | `AdminLoginComponent` | 🌐 Público — tela de login |
| `/admin` | `AdminDashboardComponent` | 🔒 Privado — `AdminGuard` |

### API REST (Backend)

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/login` | 🌐 Público | Login e geração do token JWT |
| `POST` | `/api/pedidos` | 🌐 Público | Criação de novo pedido |
| `GET` | `/api/pedidos/validar-data` | 🌐 Público | Verificar disponibilidade de data |
| `GET` | `/api/pedidos/verificar-horario` | 🌐 Público | Verificar horário de atendimento |
| `*` | `/api/admin/**` | 🔒 JWT | Endpoints administrativos |
| `*` | `/api/pedidos/admin/**` | 🔒 JWT | Gestão de pedidos (admin) |

---

## 🚀 Como Executar

**Pré-requisitos:** [Git](https://git-scm.com) · [Docker Desktop](https://www.docker.com/get-started)

> Não é necessário ter Java, Node.js ou MySQL instalados localmente.

```bash
# 1. Clone o repositório
git clone https://github.com/joaoribeiroodev/sistema-confeitaria.git
cd sistema-confeitaria

# 2. Configure as variáveis de ambiente
cp .env.example .env
# Preencha o arquivo .env com seus valores

# 3. Suba toda a stack
docker-compose up --build
```

| Serviço | URL |
|---|---|
| 🛍️ Vitrine | `http://localhost` |
| 🔧 Admin | `http://localhost/admin/login` |
| 🔌 API | `http://localhost:8080` |
| 📄 Swagger | `http://localhost:8080/swagger-ui.html` |

---

## 📄 Documentação da API

A documentação interativa é gerada automaticamente pelo **Springdoc OpenAPI**:

```
http://localhost:8080/swagger-ui.html
```

**Para testar endpoints autenticados:**
1. Faça `POST /api/auth/login` e copie o `token` da resposta
2. Clique em **🔒 Authorize** e informe `Bearer <seu-token>`

---

## 📂 Estrutura

```
sistema-confeitaria/
├── 📁 .github/workflows/        → CI/CD com GitHub Actions
├── 📁 database/                 → Script SQL de inicialização do banco
├── 📄 docker-compose.yml        → Orquestração dos containers
├── 📄 .env.example              → Variáveis necessárias (sem valores)
│
├── 📁 backend/                  ☕ Spring Boot 3 — API REST
│   └── src/main/java/.../
│       ├── config/              → SwaggerConfig
│       ├── controller/          → Endpoints REST
│       ├── dto/                 → Request / Response bodies
│       ├── model/               → Entidades JPA
│       ├── repository/          → Spring Data repositories
│       ├── security/            → JwtFilter · JwtUtil · SecurityConfig
│       └── service/             → Regras de negócio e relatórios
│
└── 📁 frontend/                 🅰️ Angular 21 — PWA
    └── src/app/
        ├── components/
        │   ├── order/           → Vitrine pública e formulário de pedido
        │   ├── admin-login/     → Tela de autenticação
        │   └── admin-dashboard/ → Painel de gestão
        ├── guards/              → AdminGuard (proteção de rota)
        ├── interceptors/        → Injeção automática do Bearer Token
        └── services/            → api · admin · cart
```

---

## 👤 Autor

<div align="center">

**João Pedro Ribeiro**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/joão-pedro-ribeiro-a62913310)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/joaoribeiroodev)

<br/>

*Desenvolvido com ❤️ e muito ☕*

</div>
