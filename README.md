<div align="center">

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=700&size=28&pause=1000&color=F97316&center=true&vCenter=true&width=600&lines=%F0%9F%8E%82+Sistema+Confeitaria;Fullstack+com+Spring+Boot+%2B+Angular;Gest%C3%A3o+de+Pedidos+%26+Produ%C3%A7%C3%A3o" alt="Typing SVG" />

<br/>

> Plataforma fullstack para digitalizar a gestão de encomendas de uma confeitaria artesanal —
> do pedido do cliente ao controle de produção, tudo em um só lugar.

<br/>

[![🌐 Demo ao Vivo](https://img.shields.io/badge/🌐_Demo_ao_Vivo-delicias--da--nalva.vercel.app-F97316?style=for-the-badge)](https://delicias-da-nalva.vercel.app/)
[![📄 Swagger Docs](https://img.shields.io/badge/📄_API_Docs-Swagger_UI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://sistema-confeitaria-backend.onrender.com/swagger-ui/index.html)
[![GitHub](https://img.shields.io/badge/Código_Fonte-181717?style=for-the-badge&logo=github)](https://github.com/joaoribeiroodev/sistema-confeitaria)

<br/>

![Build](https://img.shields.io/badge/build-passing-brightgreen?style=flat-square&logo=github-actions)
![Java](https://img.shields.io/badge/Java-39.9%25-ED8B00?style=flat-square&logo=openjdk)
![TypeScript](https://img.shields.io/badge/TypeScript-26.9%25-3178C6?style=flat-square&logo=typescript)
![HTML](https://img.shields.io/badge/HTML-29.8%25-E34F26?style=flat-square&logo=html5&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)
![Status](https://img.shields.io/badge/status-em_produção-success?style=flat-square)

</div>

---

## 🧭 Sobre o Projeto

Confeitarias artesanais gerenciam pedidos no caderno ou no WhatsApp — sem controle de capacidade, sem histórico, sem relatórios. O **Sistema Confeitaria** resolve esse problema digitalizando todo o fluxo em uma plataforma web instalável como app (PWA).

---

## ✨ Funcionalidades

### 🛍️ Vitrine Pública
- Catálogo de produtos com carrinho dinâmico
- Agendamento de data de retirada com **validação de capacidade em tempo real**
- Verificação automática de horário de atendimento antes de aceitar pedidos
- **Instalável no celular como app** via Angular Service Worker (PWA)

### 🔧 Painel Administrativo
- Login seguro com **JWT stateless** — sem sessão no servidor
- Fila de pedidos do dia com atualização de status de produção
- Dashboard com métricas e histórico de pedidos
- **Exportação de relatórios mensais em `.xlsx`** via Apache POI

### 🔒 Segurança em dois níveis
- **Backend:** `JwtFilter` intercepta toda requisição às rotas protegidas
- **Frontend:** `AdminGuard` bloqueia navegação + `HttpInterceptor` injeta o `Bearer token` automaticamente
- Swagger UI desabilitado no perfil de produção

---

## 🛠️ Stack

<div align="center">

**Backend**

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL_8-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Apache POI](https://img.shields.io/badge/Apache_POI-D22128?style=for-the-badge&logo=apache&logoColor=white)

**Frontend**

![Angular](https://img.shields.io/badge/Angular_21-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript_5.9-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_4-06B6D4?style=for-the-badge&logo=tailwind-css&logoColor=white)
![RxJS](https://img.shields.io/badge/RxJS-B7178C?style=for-the-badge&logo=reactivex&logoColor=white)
![PWA](https://img.shields.io/badge/PWA-5A0FC8?style=for-the-badge&logo=pwa&logoColor=white)

**Infraestrutura**

![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

</div>

---

## 🚀 Como Executar Localmente

> **Pré-requisitos:** [Git](https://git-scm.com) e [Docker Desktop](https://www.docker.com/get-started). Não é necessário instalar Java, Node.js ou MySQL.

```bash
git clone https://github.com/joaoribeiroodev/sistema-confeitaria.git
cd sistema-confeitaria

cp .env.example .env
# Edite o .env com seus valores

docker-compose up --build
```

| Serviço | URL |
|---|---|
| 🛍️ Vitrine | `http://localhost` |
| 🔧 Admin | `http://localhost/admin/login` |
| 🔌 API | `http://localhost:8080` |
| 📄 Swagger | `http://localhost:8080/swagger-ui.html` |

---

## 📡 Endpoints da API

<details>
<summary><strong>Ver rotas públicas e protegidas</strong></summary>

<br/>

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/login` | 🌐 Público | Login e geração do JWT |
| `POST` | `/api/pedidos` | 🌐 Público | Criar novo pedido |
| `GET` | `/api/pedidos/validar-data` | 🌐 Público | Verificar disponibilidade de data |
| `GET` | `/api/pedidos/verificar-horario` | 🌐 Público | Verificar horário de atendimento |
| `*` | `/api/admin/**` | 🔒 JWT | Endpoints administrativos |
| `*` | `/api/pedidos/admin/**` | 🔒 JWT | Gestão de pedidos (admin) |

> Documentação interativa completa: [Swagger UI →](https://sistema-confeitaria-backend.onrender.com/swagger-ui/index.html)

</details>

---

## 📂 Estrutura de Pastas

```
sistema-confeitaria/
├── .github/workflows/          → CI/CD com GitHub Actions
├── database/                   → Script SQL de inicialização
├── docker-compose.yml
├── .env.example
│
├── backend/                    ☕ Spring Boot 3.2 — API REST
│   └── src/main/java/
│       ├── config/             → SwaggerConfig
│       ├── controller/         → Endpoints REST
│       ├── dto/                → Request / Response
│       ├── model/              → Entidades JPA
│       ├── repository/         → Spring Data
│       ├── security/           → JwtFilter · JwtUtil · SecurityConfig
│       └── service/            → Regras de negócio + relatórios
│
└── frontend/                   🅰️ Angular 21 — PWA
    └── src/app/
        ├── components/
        │   ├── order/          → Vitrine pública
        │   ├── admin-login/    → Autenticação
        │   └── admin-dashboard/→ Painel admin
        ├── guards/             → AdminGuard
        ├── interceptors/       → Bearer Token automático
        └── services/           → api · admin · cart
```

---

## 👤 Autor

<div align="center">

**João Pedro Ribeiro**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/joão-pedro-ribeiro-a62913310)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/joaoribeiroodev)

*Desenvolvido com ❤️ e muito ☕*

</div>