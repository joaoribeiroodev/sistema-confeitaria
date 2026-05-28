# ✨ Delícias da Nalva — Sistema de Encomendas

> Plataforma completa de **e-commerce e gestão de encomendas** para confeitaria artesanal: vitrine pública para venda de salgados e doces sob encomenda + painel administrativo com controle de produção, faturamento e relatórios gerenciais.

---

## 🚀 Funcionalidades

- 🛒 Carrinho de compras dinâmico com agendamento de data de retirada
- 🏭 Painel admin com **fila de produção** e atualização de status em tempo real
- 📊 Dashboard com **gráficos de faturamento** e métricas de pico de vendas
- 📥 Exportação de **relatórios mensais em `.xlsx`** (Excel nativo via Apache POI)
- 🔐 Autenticação **JWT** com filtros no back-end e Route Guards + Interceptors no front-end
- 📱 **PWA** — instalável como app no celular do cliente

---

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

## ⚡ Como executar

> **Requisito:** [Docker](https://www.docker.com/get-started) instalado.

```bash
# Clone o repositório
git clone https://github.com/joaoribeiroodev/sistema-confeitaria.git
cd sistema-confeitaria

# Configure as variáveis de ambiente
cp .env.example .env

# Suba todos os serviços (banco, back-end e front-end)
docker-compose up --build
```

| Serviço | URL |
|---|---|
| 🛍️ Vitrine Pública | `http://localhost:4200` |
| 🔌 API REST | `http://localhost:8080` |

---

## 📂 Estrutura

```
sistema-confeitaria/
├── .github/workflows/      # CI/CD com GitHub Actions
├── database/init.sql       # Script de inicialização do banco
├── docker-compose.yml      # Orquestração dos containers
├── backend/                # ☕ Spring Boot (API REST + Segurança + Relatórios)
│   └── src/.../
│       ├── controller/     # Endpoints REST
│       ├── security/       # JWT Filter, Spring Security Config
│       ├── service/        # Regras de negócio e geração de relatórios
│       └── model/          # Entidades JPA
└── frontend/               # 🅰️ Angular PWA
    └── src/app/
        ├── components/     # Telas (vitrine, admin-dashboard, login)
        ├── guards/         # Proteção de rotas administrativas
        ├── interceptors/   # Injeção automática do Bearer Token
        └── services/       # Comunicação com a API
```

---

<div align="center">
  <p>Desenvolvido com ❤️ e muito ☕ por <strong>João Pedro Ribeiro</strong></p>
</div>
