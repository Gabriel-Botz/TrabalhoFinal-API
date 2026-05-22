# 🎬 SerratecFlix — Documentação de Planejamento Interno

> **⚠️ ATENÇÃO ANTES DE TUDO**
> O PDF do projeto proíbe explicitamente o uso de IA para geração de **código, documentação, modelagem e implementação**.
> Este arquivo é um **guia de planejamento interno** para organizar o time — ele **não deve ser entregue** como documentação do projeto.
> O README.md, o Swagger e qualquer documentação oficial **devem ser escritos pelo grupo**, com suas próprias palavras.

---

## 1. Visão Geral

**Nome:** SerratecFlix  
**Tipo:** API RESTful  
**Stack:** Java + Spring Boot + PostgreSQL  
**Prazo:** 27/05/2026 às 23:59  
**Pontuação:** 55 pts (45 grupo + 10 individual)  
**Integrantes:** 6 pessoas

A SerratecFlix é uma plataforma de streaming geek onde usuários podem avaliar filmes e séries, criar listas personalizadas de favoritos, acompanhar watchlists e consultar informações externas via API. O backend é uma API RESTful que expõe todos esses dados de forma segura, documentada e organizada.

---

## 2. Tecnologias e Dependências (pom.xml)

| Dependência                           | Finalidade                 |
| ------------------------------------- | -------------------------- |
| `spring-boot-starter-web`             | Controllers REST           |
| `spring-boot-starter-data-jpa`        | JPA + Hibernate            |
| `spring-boot-starter-security`        | Spring Security            |
| `spring-boot-starter-validation`      | Bean Validation            |
| `jjwt` (io.jsonwebtoken)              | Geração e validação de JWT |
| `springdoc-openapi-starter-webmvc-ui` | Swagger / OpenAPI          |
| `postgresql`                          | Driver do banco            |
| `lombok`                              | Redução de boilerplate     |
| `spring-boot-devtools`                | Reload automático em dev   |

**Banco de dados:** PostgreSQL  
**Build:** Maven  
**Versionamento:** Git + GitHub

---

## 3. Modelagem de Dados

### 3.1 Entidades

#### 👤 Usuario

| Campo       | Tipo          | Restrições               |
| ----------- | ------------- | ------------------------ |
| id          | Long          | PK, auto gerado          |
| nome        | String        | @NotBlank                |
| email       | String        | @NotBlank, @Email, único |
| username    | String        | @NotBlank, único         |
| senha       | String        | @NotBlank, @Size(min=6)  |
| fotoPerfil  | String        | Opcional (URL ou path)   |
| dataCriacao | LocalDateTime | Auto preenchido          |

#### 🎬 Filme

| Campo                   | Tipo      | Restrições                                     |
| ----------------------- | --------- | ---------------------------------------------- |
| id                      | Long      | PK, auto gerado                                |
| titulo                  | String    | @NotBlank                                      |
| descricao               | String    | @NotBlank                                      |
| duracao                 | Integer   | @NotNull (em minutos)                          |
| dataLancamento          | LocalDate | @NotNull                                       |
| classificacaoIndicativa | Enum      | LIVRE, DEZ, DOZE, QUATORZE, DEZESSEIS, DEZOITO |
| notaMedia               | Double    | Calculado a partir das avaliações              |

#### 📺 Serie

| Campo          | Tipo      | Restrições                        |
| -------------- | --------- | --------------------------------- |
| id             | Long      | PK, auto gerado                   |
| titulo         | String    | @NotBlank                         |
| descricao      | String    | @NotBlank                         |
| temporadas     | Integer   | @NotNull                          |
| episodios      | Integer   | @NotNull                          |
| dataLancamento | LocalDate | @NotNull                          |
| notaMedia      | Double    | Calculado a partir das avaliações |

#### 🏷️ Categoria

| Campo     | Tipo   | Restrições                   |
| --------- | ------ | ---------------------------- |
| id        | Long   | PK, auto gerado              |
| nome      | String | @NotBlank (ex: Ação, Terror) |
| descricao | String | @NotBlank                    |

#### ⭐ AvaliacaoFilme

| Campo         | Tipo          | Restrições              |
| ------------- | ------------- | ----------------------- |
| id            | Long          | PK, auto gerado         |
| nota          | Double        | @NotNull, min=0, max=10 |
| comentario    | String        | @NotBlank               |
| dataAvaliacao | LocalDateTime | Auto preenchido         |
| usuario       | Usuario       | FK (ManyToOne)          |
| filme         | Filme         | FK (ManyToOne)          |

#### ⭐ AvaliacaoSerie

| Campo         | Tipo          | Restrições              |
| ------------- | ------------- | ----------------------- |
| id            | Long          | PK, auto gerado         |
| nota          | Double        | @NotNull, min=0, max=10 |
| comentario    | String        | @NotBlank               |
| dataAvaliacao | LocalDateTime | Auto preenchido         |
| usuario       | Usuario       | FK (ManyToOne)          |
| serie         | Serie         | FK (ManyToOne)          |

#### 📋 ListaFavoritos

| Campo       | Tipo          | Restrições                            |
| ----------- | ------------- | ------------------------------------- |
| id          | Long          | PK, auto gerado                       |
| nomeLista   | String        | @NotBlank (ex: Favoritos, Top Filmes) |
| privada     | Boolean       | @NotNull                              |
| dataCriacao | LocalDateTime | Auto preenchido                       |
| usuario     | Usuario       | FK (ManyToOne)                        |

---

### 3.2 Relacionamentos

| Entidade A     | Tipo        | Entidade B     | Dono                 |
| -------------- | ----------- | -------------- | -------------------- |
| Usuario        | @OneToMany  | AvaliacaoFilme | AvaliacaoFilme       |
| Usuario        | @OneToMany  | AvaliacaoSerie | AvaliacaoSerie       |
| Filme          | @OneToMany  | AvaliacaoFilme | AvaliacaoFilme       |
| Serie          | @OneToMany  | AvaliacaoSerie | AvaliacaoSerie       |
| Filme          | @ManyToMany | Categoria      | tabela intermediária |
| Serie          | @ManyToMany | Categoria      | tabela intermediária |
| Usuario        | @OneToMany  | ListaFavoritos | ListaFavoritos       |
| ListaFavoritos | @ManyToMany | Filme          | tabela intermediária |
| ListaFavoritos | @ManyToMany | Serie          | tabela intermediária |

> ⚠️ **Atenção:** usar `@JsonManagedReference` / `@JsonBackReference` ou DTOs para **evitar recursão infinita** nos relacionamentos. Nunca retornar a entidade diretamente no controller — usar sempre DTOs.

---

## 4. Arquitetura de Pacotes

```
com.serratecflix
├── config/           → Configurações gerais (CORS, beans, DataLoader)
├── security/         → JWT (filter, util, UserDetailsService)
├── entity/           → Entidades JPA
├── repository/       → Interfaces JPA (Query Methods, JPQL, Native Query)
├── service/          → Regras de negócio
├── controller/       → Endpoints REST
├── dto/
│   ├── request/      → DTOs de entrada (POST/PUT)
│   └── response/     → DTOs de saída (GET)
└── exception/        → Exceptions customizadas + @ControllerAdvice
```

---

## 5. Endpoints por Entidade

> Padrão obrigatório: GET all, GET by ID, POST, PUT, DELETE para cada entidade.

### 🔓 Autenticação (pública)

| Método | Rota             | Descrição             |
| ------ | ---------------- | --------------------- |
| POST   | `/auth/login`    | Retorna JWT           |
| POST   | `/auth/register` | Cadastra novo usuário |

### 👤 Usuario (protegido)

| Método | Rota            | Descrição      |
| ------ | --------------- | -------------- |
| GET    | `/Usuario`      | Lista todos    |
| GET    | `/Usuario/{id}` | Busca por ID   |
| PUT    | `/Usuario/{id}` | Atualiza dados |
| DELETE | `/Usuario/{id}` | Remove usuário |

### 🎬 Filmes (protegido)

| Método | Rota           | Descrição      |
| ------ | -------------- | -------------- |
| GET    | `/filmes`      | Lista todos    |
| GET    | `/filmes/{id}` | Busca por ID   |
| POST   | `/filmes`      | Cadastra filme |
| PUT    | `/filmes/{id}` | Atualiza filme |
| DELETE | `/filmes/{id}` | Remove filme   |

### 📺 Series (protegido)

| Método | Rota           | Descrição      |
| ------ | -------------- | -------------- |
| GET    | `/series`      | Lista todas    |
| GET    | `/series/{id}` | Busca por ID   |
| POST   | `/series`      | Cadastra série |
| PUT    | `/series/{id}` | Atualiza série |
| DELETE | `/series/{id}` | Remove série   |

### 🏷️ Categorias (protegido)

| Método | Rota               | Descrição          |
| ------ | ------------------ | ------------------ |
| GET    | `/categorias`      | Lista todas        |
| GET    | `/categorias/{id}` | Busca por ID       |
| POST   | `/categorias`      | Cadastra categoria |
| PUT    | `/categorias/{id}` | Atualiza categoria |
| DELETE | `/categorias/{id}` | Remove categoria   |

### ⭐ Avaliações de Filme (protegido)

| Método | Rota                      | Descrição          |
| ------ | ------------------------- | ------------------ |
| GET    | `/avaliacoes/filmes`      | Lista todas        |
| GET    | `/avaliacoes/filmes/{id}` | Busca por ID       |
| POST   | `/avaliacoes/filmes`      | Cria avaliação     |
| PUT    | `/avaliacoes/filmes/{id}` | Atualiza avaliação |
| DELETE | `/avaliacoes/filmes/{id}` | Remove avaliação   |

### ⭐ Avaliações de Serie (protegido)

| Método | Rota                      | Descrição          |
| ------ | ------------------------- | ------------------ |
| GET    | `/avaliacoes/series`      | Lista todas        |
| GET    | `/avaliacoes/series/{id}` | Busca por ID       |
| POST   | `/avaliacoes/series`      | Cria avaliação     |
| PUT    | `/avaliacoes/series/{id}` | Atualiza avaliação |
| DELETE | `/avaliacoes/series/{id}` | Remove avaliação   |

### 📋 Listas de Favoritos (protegido)

| Método | Rota                            | Descrição              |
| ------ | ------------------------------- | ---------------------- |
| GET    | `/listas`                       | Lista todas            |
| GET    | `/listas/{id}`                  | Busca por ID           |
| POST   | `/listas`                       | Cria lista             |
| PUT    | `/listas/{id}`                  | Atualiza lista         |
| DELETE | `/listas/{id}`                  | Remove lista           |
| POST   | `/listas/{id}/filmes/{filmeId}` | Adiciona filme à lista |
| POST   | `/listas/{id}/series/{serieId}` | Adiciona série à lista |

---

## 6. Regras de Negócio

1. **Nota de avaliação** → obrigatoriamente entre **0 e 10** (validar com `@Min(0)` e `@Max(10)`)
2. **notaMedia** de Filme/Serie → calculada automaticamente pela média das avaliações vinculadas (via JPQL ou lógica no service)
3. **username** e **email** do Usuario → devem ser únicos (lançar `409 Conflict` em duplicata)
4. **ListaFavoritos.privada** → se `true`, só o dono pode visualizar (regra de negócio no service)
5. **Senha** → tamanho mínimo de 6 caracteres; deve ser armazenada com hash (BCrypt)
6. **Rotas protegidas** → todas exceto `/auth/login` e `/auth/register` exigem JWT válido no header `Authorization: Bearer {token}`
7. **Recursão infinita** → proibido retornar entidades com relacionamentos circulares — usar DTOs sempre
8. **Banco pré-populado** → ao iniciar a aplicação, deve haver registros cadastrados automaticamente

---

## 7. Tratamento de Exceções

| Situação                          | Exception customizada                    | Status HTTP |
| --------------------------------- | ---------------------------------------- | ----------- |
| Entidade não encontrada           | `ResourceNotFoundException`              | 404         |
| Dados inválidos / validação       | Tratado pelo `@Valid` + ControllerAdvice | 400         |
| username/email duplicado          | `ConflictException`                      | 409         |
| Sem autenticação / token inválido | Tratado pelo Spring Security             | 401         |

Implementar um `GlobalExceptionHandler` com `@ControllerAdvice` que padronize todas as respostas de erro no formato:

```json
{
  "timestamp": "2026-05-21T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "O campo nota deve ser entre 0 e 10",
  "path": "/avaliacoes/filmes"
}
```

---

## 8. API Externa

Consumir obrigatoriamente pelo menos uma API externa. Sugestão mais simples:

- **ViaCEP** (`https://viacep.com.br/ws/{cep}/json/`) — pode ser usada no cadastro de usuário para buscar/validar CEP

Alternativa temática:

- **TMDB API** (`https://www.themoviedb.org/documentation/api`) — buscar informações de filmes/séries por título

Implementar via `RestTemplate` ou `WebClient` no pacote `service` ou criar um `ExternalApiService` separado.

---

## 9. Segurança (JWT)

Fluxo:

1. Usuário faz POST em `/auth/login` com `{ "username": "...", "senha": "..." }`
2. Spring Security valida as credenciais via `UserDetailsService`
3. Se válido, retorna `{ "token": "eyJ..." }`
4. Todas as requisições subsequentes enviam o header: `Authorization: Bearer eyJ...`
5. Um `JwtAuthenticationFilter` intercepta cada request, valida o token e autentica o usuário no contexto do Spring

Classes necessárias no pacote `security`:

- `JwtUtil` — gera e valida tokens
- `JwtAuthenticationFilter` — filtro que intercepta as requisições
- `UserDetailsServiceImpl` — carrega usuário do banco
- `SecurityConfig` — configura quais rotas são públicas/protegidas

---

## 10. Divisão de Tarefas

> ⚡ **Prazo real:** 6 dias (hoje é 21/05, entrega 27/05 às 23:59)
> A divisão foi pensada para minimizar conflitos de merge e maximizar paralelismo.

---

### 👤 Integrante 1 — Setup Geral + Segurança + JWT

**Responsabilidade principal:** Estrutura base do projeto e autenticação

**Entregáveis:**

- Criar o projeto Spring Boot no Spring Initializr com todas as dependências
- Configurar `application.properties` (banco, JWT secret, porta)
- Implementar os pacotes `security/` e `config/`
- Classes: `SecurityConfig`, `JwtUtil`, `JwtAuthenticationFilter`, `UserDetailsServiceImpl`
- Endpoints públicos: `POST /auth/login` e `POST /auth/register`
- Configurar Swagger com suporte a Bearer token (para que o time todo já possa testar autenticado)

**Dependências:** Nenhuma — pode começar imediatamente. É o ponto de partida de todos os outros.

---

### 👤 Integrante 2 — Entidade Usuario + ListaFavoritos

**Responsabilidade principal:** CRUD completo de Usuário e Listas de Favoritos

**Entregáveis:**

- Pacotes `entity`, `repository`, `service`, `controller`, `dto` para `Usuario` e `ListaFavoritos`
- Endpoint de adicionar Filme/Serie à lista (`POST /listas/{id}/filmes/{filmeId}`)
- Validações: `@NotBlank`, `@Email`, `@Size`, `@NotNull` nos DTOs
- Exception para username/email duplicado (409)

**Dependências:** Aguardar Integrante 1 configurar o projeto base e o `SecurityConfig` antes de subir os controllers protegidos.

---

### 👤 Integrante 3 — Entidade Filme + Categoria

**Responsabilidade principal:** CRUD de Filme e Categoria, e o relacionamento ManyToMany entre eles

**Entregáveis:**

- Pacotes completos para `Filme` e `Categoria`
- Enum `ClassificacaoIndicativa` no pacote `entity`
- Relacionamento `@ManyToMany` entre Filme e Categoria (tabela intermediária `filme_categoria`)
- Endpoint para vincular categorias a um filme
- Query Method ou JPQL para buscar filmes por categoria

**Dependências:** Aguardar Integrante 1 (projeto base). Integrante 4 vai usar a entidade `Categoria` criada aqui — comunicar quando estiver commitada.

---

### 👤 Integrante 4 — Entidade Serie

**Responsabilidade principal:** CRUD completo de Serie e vínculo com Categoria

**Entregáveis:**

- Pacotes completos para `Serie`
- Relacionamento `@ManyToMany` com `Categoria` (reutilizar entidade do Integrante 3)
- Endpoint para vincular categorias a uma série
- Query Method ou JPQL para buscar séries por categoria ou por número de temporadas

**Dependências:** Aguardar Integrante 3 commitar a entidade `Categoria` antes de criar o relacionamento de `Serie` com ela.

---

### 👤 Integrante 5 — Avaliações + Cálculo de NotaMedia + Queries

**Responsabilidade principal:** CRUD de AvaliacaoFilme e AvaliacaoSerie, cálculo de média, e queries obrigatórias

**Entregáveis:**

- Pacotes completos para `AvaliacaoFilme` e `AvaliacaoSerie`
- Validação de nota entre 0 e 10 (`@Min`, `@Max` ou validação no service)
- Lógica de recalcular `notaMedia` de Filme/Serie após cada avaliação criada/editada/deletada
- Pelo menos uma **JPQL query**: ex. `SELECT AVG(a.nota) FROM AvaliacaoFilme a WHERE a.filme.id = :filmeId`
- Pelo menos uma **Native Query**: ex. buscar as 5 avaliações mais recentes de um filme
- Pelo menos um **Query Method**: ex. `findByUsuarioId(Long usuarioId)`

**Dependências:** Aguardar Integrante 2 (Usuario), Integrante 3 (Filme) e Integrante 4 (Serie) commitarem suas entidades.

---

### 👤 Integrante 6 — Exception Handler + API Externa + DataLoader + README

**Responsabilidade principal:** Infraestrutura de erros, dados iniciais e consumo de API externa

**Entregáveis:**

- `GlobalExceptionHandler` com `@ControllerAdvice` (padrão de resposta de erro definido no item 7)
- Exceptions customizadas: `ResourceNotFoundException`, `ConflictException`
- `DataLoader` (via `CommandLineRunner` ou `data.sql`) com pelo menos: 3 usuários, 5 filmes, 3 séries, 4 categorias, avaliações e listas
- Consumo de API externa (ViaCEP ou TMDB) integrado a algum endpoint (ex.: enriquecer dados do usuário com CEP, ou buscar poster de filme)
- Auxiliar o time na escrita do README.md final (sem usar IA)

**Dependências:** O `GlobalExceptionHandler` pode ser feito desde o início. O `DataLoader` aguarda as entidades de todos os outros integrantes estarem prontas (última fase).

---

## 11. Cronograma Sugerido (6 dias)

> Dado o prazo curto, o foco deve ser: **funcionar primeiro, refinar depois.**

### Dia 1 — 21/05 (hoje) · Fundação

- [ ] Integrante 1: criar projeto, configurar banco, subir no GitHub, criar `develop` branch
- [ ] Definir padrão de branches: `feature/nome-da-feature` → PR para `develop`
- [ ] Definir padrão de commit: `feat: descrição`, `fix: descrição`, `chore: descrição`
- [ ] Todo o grupo: clonar o repositório e confirmar que roda localmente

### Dia 2 — 22/05 · Core das Entidades

- [ ] Integrante 1: Security + JWT funcional (login retornando token)
- [ ] Integrante 2: Entities + Repositories de Usuario e ListaFavoritos
- [ ] Integrante 3: Entities + Repositories de Filme, Categoria e Enum
- [ ] Integrante 4: Entity + Repository de Serie
- [ ] Integrante 5: Entities + Repositories de AvaliacaoFilme e AvaliacaoSerie
- [ ] Integrante 6: `GlobalExceptionHandler` base + exceptions customizadas

### Dia 3 — 23/05 · Services e Controllers

- [ ] Todos: implementar os Services e Controllers das suas entidades
- [ ] Integrante 5: implementar JPQL, Native Query e Query Methods
- [ ] Integrante 6: iniciar consumo da API externa

### Dia 4 — 24/05 · Integração e Testes

- [ ] Testar todos os endpoints no Swagger
- [ ] Verificar que não há recursão infinita nas respostas
- [ ] Corrigir bugs de integração (relacionamentos, FKs)
- [ ] Integrante 6: finalizar DataLoader com dados de teste

### Dia 5 — 25/05 · Ajustes Finos

- [ ] Validar todos os status HTTP (400, 401, 404, 409)
- [ ] Verificar validações Bean Validation em todos os DTOs
- [ ] Swagger documentado e funcional com autenticação
- [ ] Revisar mensagens de commit — todos devem ter commits em dias diferentes

### Dia 6 — 26/05 · Entrega

- [ ] README.md escrito e revisado pelo grupo (sem IA)
- [ ] Merge final de todas as branches para `main`
- [ ] Testar do zero: clonar o repo, rodar, confirmar que tudo funciona
- [ ] Submeter link do GitHub antes das 23:59 do dia 27/05

---

## 12. Pontos de Atenção e Ambiguidades do PDF

| #   | Ponto                    | Observação                                                                                                                            |
| --- | ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | `notaMedia`              | O PDF lista o campo mas não especifica como calcular. Sugestão: calcular no service a cada CREATE/UPDATE/DELETE de avaliação          |
| 2   | `fotoPerfil`             | Opcional — pode ser uma String (URL) ou implementar upload real (funcionalidade extra)                                                |
| 3   | API externa              | O PDF cita ViaCEP como sugestão, mas qualquer outra serve. Definir em equipe qual usar e onde integrá-la                              |
| 4   | Parte individual         | Cada pessoa precisa de uma funcionalidade **diferente** das obrigatórias com GET/POST/PUT/DELETE. Planejar cedo para não ter conflito |
| 5   | `ListaFavoritos.privada` | O PDF define o campo mas não especifica a regra de negócio. Sugestão: se `privada = true`, apenas o dono pode acessar via token       |
| 6   | Classificação indicativa | O PDF lista como ENUM mas não define os valores exatos. Sugestão: `LIVRE, DEZ, DOZE, QUATORZE, DEZESSEIS, DEZOITO`                    |

---

## 13. Checklist Final de Entrega

### Obrigatórios (risco de zero se faltar)

- [ ] Projeto compila e executa sem erros
- [ ] Commits em dias diferentes com mensagens coerentes (não pode ser um commit único!)
- [ ] README no GitHub (escrito pelo grupo, sem IA)
- [ ] Código desenvolvido sem uso de IA

### Obrigatórios (desconto de pontos se faltar)

- [ ] Todos os CRUDs funcionando (GET, GET by ID, POST, PUT, DELETE)
- [ ] Arquitetura em camadas respeitada (entity, repository, service, controller, dto, exception, config, security)
- [ ] DTOs em todas as respostas (nunca retornar entidade diretamente)
- [ ] Spring Security + JWT funcionando
- [ ] Pelo menos uma rota pública
- [ ] Bean Validation nos campos obrigatórios
- [ ] @ControllerAdvice com respostas padronizadas
- [ ] Status HTTP corretos (400, 401, 404, 409)
- [ ] Swagger documentado e com autenticação
- [ ] Query Methods, JPQL e Native Query presentes
- [ ] Consumo de API externa
- [ ] Banco pré-populado (DataLoader ou data.sql)
- [ ] Sem recursão infinita nas respostas JSON
- [ ] Relacionamentos JPA corretos

### Extras (impactam criatividade/diferencial)

- [ ] Paginação
- [ ] Upload de imagem
- [ ] Filtros avançados
- [ ] Envio de e-mail
