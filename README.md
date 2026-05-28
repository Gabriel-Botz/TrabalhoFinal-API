# 🎬 SerratecFlix — API RESTful

<p align="center"> API de streaming geek com autenticação, avaliações e integração externa 🚀 </p> <p align="center"> <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/> <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/> <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"/> <img src="https://img.shields.io/badge/Security-JWT-black?style=for-the-badge"/> <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge"/> </p>

<img width="1905" height="1079" alt="image" src="https://github.com/user-attachments/assets/d579b1ef-8180-408b-9e67-2ff5f9bcdd68" />


## 📌 Sobre o Projeto

O SerratecFlix é uma API RESTful completa desenvolvida como projeto final do módulo de Backend do Serratec.

### A aplicação simula uma plataforma de streaming com foco no universo geek, permitindo que usuários:

- 🎬 Avaliem filmes e séries
- ⭐ Visualizem rankings de conteúdo
- 📋 Criem listas personalizadas
- ❤️ Favoritem produções
- 🌐 Consumam dados de APIs externas

> 💡 O projeto foi estruturado com foco em boas práticas de mercado, arquitetura em camadas e segurança com autenticação JWT.

## 💡 Diferencial do Projeto

Mesmo não sendo obrigatório, o projeto conta com:

#### 🌐 Interface Front-end

- Interface web para consumo da API
- Validação completa do fluxo de autenticação
- Experiência visual moderna

`📁 Local: ./serratecflix-front`

---

### 🔧 Stack Tecnológica

- **Linguagem:** Java 17+
- **Framework Principal:** Spring Boot 3.x
- **Persistência & ORM:** Spring Data JPA / Hibernate
- **Banco de Dados:** PostgreSQL (Produção/Dev)
- **Segurança:** Spring Security + JWT (Tokens de Autenticação)
- **Validação:** Bean Validation (`@Valid`, `@NotBlank`, `@Size`, etc.)
- **Documentação:** Swagger UI / OpenAPI 3.0
- **Gerenciador de Dependências:** Maven

### Arquitetura & Boas Práticas

O projeto segue arquitetura em camadas, promovendo organização e escalabilidade:

```bash
serratecflix/
  ├── config/          # Configurações gerais da aplicação
  ├── controller/      # Endpoints da API (REST Controllers)
  ├── dto/             # Objetos de Transferência de Dados (Request/Response)
  ├── entity/          # Modelos de dados / Entidades JPA
  ├── exception/       # Tratamento global de erros (@ControllerAdvice)
  ├── repository/      # Interfaces de acesso ao banco (Query Methods/JPQL)
  ├── security/        # Filtros, JWT, UserDetailsService e configurações de rotas
  └── service/         # Camada de regras de negócio da aplicação
```

## 🚀 Como Executar o Projeto

- #### **✔️Pré-requisitos**
  - Java 17 ou superior instalado.
  - PostgreSQL rodando localmente _(ou via Docker)_.
  - Clone o repositório:

```bash
git clone https://github.com/Gabriel-Botz/TrabalhoFinal-API.git
cd TrabalhoFinal-API
```

## ⚙️ Configuração

Configure as variáveis de ambiente:

```bash
PORT=8080
DB_URL=jdbc:postgresql://localhost:5432/seu_banco
DB_USERNAME=postgres
DB_PASSWORD=senha

EMAIL_SERVIDOR=smtp.seuprovedor.com
EMAIL_PORT=587
EMAIL_USER=seu_email
EMAIL_PASSWORD=sua_senha

JWT_SECRET=sua_chave_secreta
```

## 🗄️ Banco de Dados

✔️ O sistema inicia com dados automáticos via data.sql ou import.sql.

#### Isso permite:

- Testar endpoints imediatamente
- Validar regras de negócio rapidamente

### 📖 Documentação da API

Acesse via Swagger:

```bash
➡️ http://localhost:8080/swagger-ui/index.html
```

#### ✔️ Contém:

- Endpoints completos
- Exemplos de requisição
- Autenticação JWT

#### 🔐 Segurança

A aplicação utiliza:

- 🔑 JWT (Bearer Token)
- 🔒 Spring Security
- 🔐 Rotas protegidas por autenticação

### 🔓 Rotas Públicas

- POST /api/auth/login - Autenticação do usuário e geração do Token JWT.
- POST /api/usuarios - Cadastro de novos usuários na plataforma.
- GET /api/filmes e /api/series - Listagem geral do catálogo.

### 📡 Exemplos de Endpoints

##### 🔓 Públicos

| Método | Endpoint      | Descrição                   |
| :----- | :------------ | :-------------------------- |
| POST   | /api/login    | Login e geração de token    |
| POST   | /api/usuarios | Cadastro de usuário         |
| GET    | /api/filmes   | Listagem de filmes públicas |
| GET    | /api/series   | Listagem de séries públicas |

##### 🔓 Públicos

| Método | Endpoint               | Descrição         |
| :----- | :--------------------- | :---------------- |
| POST   | /api/filmes            | Criar filme       |
| POST   | /api/avaliacoes/filmes | Avaliar filme     |
| GET    | /api/listas            | Listas do usuário |

## 🌐 Integração com API Externa

A API consome dados da:

- 🎬 TMDB (The Movie Database)

Utilizada para:

- Enriquecer dados de filmes
- Buscar informações externas

## ⚠️ Tratamento de Erros

Implementado com @ControllerAdvice:

| Método | Situação                    |
| :----- | :-------------------------- |
| 400    | Dados inválidos             |
| 401    | Não autenticado             |
| 404    | Recurso não encontrado      |
| 409    | Conflito (dados duplicados) |

---

## 👥 Integrantes e Divisão de Responsabilidades

O projeto foi construído colaborativamente por um grupo de 6 desenvolvedores. Cada integrante ficou responsável por uma entidade core do sistema, sua respectiva regra de negócio, arquitetura em camadas e funcionalidade individual (requisito de 10 pontos).

| Integrante                                                      | Entidade Responsável | Funcionalidade Individual Implementada                                         |
| :-------------------------------------------------------------- | :------------------- | :----------------------------------------------------------------------------- |
| **[Lucas da Silva](https://github.com/Phonedison)**            | 👤 `Usuario`         | Envio de e-mails personalizado (CRUD Usuário)                                  |
| **[Gabriel Botelho](https://github.com/Gabriel-Botz)**       | 🎬 `Filme`           | Filtro de elenco com base no filme                                             |
| **[Paulo Cesar](https://github.com/paulocesar-neto)**          | 📺 `Serie`           | Sistema de recomendação de série                                               |
| **[Rafael Albino](https://github.com/albino57)**               | 🏷️ `Categoria`       | Pedido de mídia física                                                         |
| **[Vinícius Lammas](https://github.com/vLamass)**             | ⭐ `Avaliacao`       | Lista dos melhores filmes e a sua maior nota de avaliação                      |
| **[Elionardo dos Santos](https://github.com/elionardosantos)** | 📋 `ListaFavoritos`  | Manipulação de listas (EndPois para adicionar/remover filme/seria a uma lista) |

---
