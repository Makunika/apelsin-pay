# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

"Апельсин Pay" — backend of a demo bank / payment-gateway (graduation project, deployed at `graduate.pshiblo.xyz`). Spring Boot 2.6.3 + Spring Cloud 2021.0.1, **Java 11**, Maven multi-module (`ru.pshiblo`). The React frontends live in a sibling repo `apelsin-pay-front`; the auth-service also embeds its own React login page. UI strings, commit messages and UML labels are in Russian.

## Build & run

There is no working Maven wrapper: `mvnw` is not executable and `.mvn/wrapper/` is gitignored, so use a system `mvn` with a JDK 11 (the machine may have a newer JDK; Lombok/Boot 2.6 are only known to work on 11).

```bash
mvn -q package -DskipTests                       # all modules, jars in */target
mvn -q package -DskipTests -pl transaction-service -am   # one service + the libs it needs
mvn test -pl transaction-service                 # the only module with a test (a contextLoads smoke test)
```

Reactor order matters: `common-library` → `security-library` → `account-starter` / `feign-starter` → services. Always pass `-am` when building a single service.

Building `auth-service` runs `frontend-maven-plugin` (downloads Node 16 into `auth-service/src/main/resources/WEB-INF/view/react/node/`, runs `npm install`, `npm run lint:fix`, `npm run build`). It's slow and needs network; skip it with `-pl` when you're not touching auth.

### Docker

Dockerfiles are single-stage and only `COPY ./target/<jar>` — run `mvn package` first. Jar names are inconsistent (`auth-service.jar`, `config.jar` via `<finalName>` vs `users-service-1.0.jar`, `transaction-service-0.0.1-SNAPSHOT.jar`); check the module's `Dockerfile` `JAR_FILE` before renaming artifacts. Each service has a duplicate `dockerfile`/`Dockerfile` pair; keep them in sync or delete one.

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml --env-file .env up --build   # local build + Prometheus/Grafana
docker compose --env-file .env up                                                            # pulls makuni/apelsin-*:latest from Docker Hub
```

Compose reads secrets from the committed `.env`. `IP_DB` there points at a real Yandex Cloud Postgres — Postgres is **not** in compose; you need your own instance (5 databases: `maxbank-users`, `maxbank-transactions`, `info-personal`, `info-business`, `apelsin-payment`, user name = db name) or override `IP_DB`/`PORT_DB`/`DB_PASSWORD`.

### Running from the IDE

`.spring_run/*.run.xml` are IntelliJ configs: profile `local`, env loaded from `.env` via the EnvFile plugin. Start order: config (8888) → registry (8761) → auth-service (5000) → everything else. `local` profile expects RabbitMQ, Eureka and auth on `localhost`; `docker` profile uses compose service names. Profile selection lives in each service's `src/main/resources/application.yml` (`spring.config.import: optional:configserver:...`); everything else comes from the config server.

## Architecture

### Where configuration lives

`config-service` is a Spring Cloud Config Server in `native` mode serving `config-service/src/main/resources/shared/*.yml`. **Ports, DB names, OAuth client scopes, actuator, RabbitMQ and Eureka wiring for every service are there, not in the service modules.** `shared/application.yml` holds the docker/local profile split shared by all. When you add a service, add `shared/<app-name>.yml` and a client entry in auth-service's `AuthorizationServerConfig`.

Ports: config 8888, registry 8761, gateway 8080, auth 5000, users 5001, transaction 6001, account-business 6002, account-personal 6003, info-personal 6004, info-business 6005, payment 6006.

### Request path

Frontend → `gateway-service` (Spring Cloud Gateway). There are **no explicit routes**: `gateway.yml` enables the discovery locator with `include-expression: serviceId.endsWith('-SERVICE')`, so every Eureka-registered `*-service` is exposed at `/<service-id>/**` with the prefix stripped (e.g. `/transaction-service/api/transaction/...`). auth-service is reachable both through the gateway and directly (nginx vhost `login.graduate.pshiblo.xyz`).

### Auth

`auth-service` is a **legacy Spring Security OAuth2 authorization server** (`spring-security-oauth2` 2.3.x, `@EnableAuthorizationServer`), not the new spring-authorization-server. Clients are in-memory in `AuthorizationServerConfig`:
- `browser_main` — authorization_code, scope `user` (main UI); `browser_payment` — implicit, scope `user_payment` (payment page).
- one `client_credentials` client per service, secret = `<NAME>_SERVICE_PASSWORD` env var, scope `server` plus per-target scopes.

Tokens are RSA JWTs. Keys come from `./.keys/{private,public}.key` (`auth.secret.path`); if missing, `KeyGeneratorServiceImpl` generates a new pair. Other services fetch the public key from `GET /oauth/token_key_new` (custom endpoint, PEM without newlines). Custom claims: `id`, `email`, `name`, `status`, `lock`.

auth-service has no user table of its own: `UserServiceImpl` builds the principal by Feign-calling `users-service` (credentials) and `info-personal-service` (status/lock). Passwords are HmacSHA512 with `AUTH_SHA_SALT` (`Hmac512PasswordEncoder`, duplicated in users-service and auth-service — change both).

`security-library` (auto-configured in every business service via `spring.factories`) is the resource-server side: CORS on, CSRF off, `/actuator/**`, `/public/**`, `/error/**` open, **everything else requires `ROLE_UNBAN`**. `CustomJwtGrantedAuthoritiesConverter` grants `ROLE_UNBAN` when claim `lock=false` or scope `server` — that's the ban mechanism. Use `@IsAdmin` / `@IsModer` / `@IsUser` and `AuthUtils.getAuthUser()`.

### Shared libraries

- `common-library` — `ApelsinException` hierarchy (NotFound 404, AlreadyExist 400, Security/NotAllowedOperation 403, Internal 500) and `ExceptionControllerAdvice` (auto-configured) that maps them plus validation errors to a `{status,message,violations[]}` body. `RequestMapper`/`ResponseMapper`/`CommonMapper` are the interfaces every MapStruct mapper implements.
- `feign-starter` — `@EnableFeignClients`, an `OAuth2FeignRequestInterceptor` that attaches a client_credentials token to every Feign call, and `CustomErrorDecoder` turning non-2xx into `IntegrationException(status, body)`, which `ExceptionControllerAdvice` re-emits with the original status. This is how errors propagate across services. `transaction-service` and `auth-service` don't use the starter and carry their own copies of this config.
- `account-starter` — the shared account domain (`Account`, `HoldMoney` entities, `AccountService`, `Currency`, `CurrencyServiceImpl` pulling CBR rates daily at 08:00). Embedded in transaction-service and both account services, which is why they share the `maxbank-transactions` DB and the `accounts`/`hold_money` tables.

### Services

| service | DB | role |
|---|---|---|
| users-service | maxbank-users | logins, password hashes, roles, password-reset emails (SMTP creds in its `application.yml`) |
| info-personal-service | info-personal | person profile/KYC, moderation; drives registration via Feign → users-service |
| info-business-service | info-business | companies, company users, moderation history, merchant `apiKey` check endpoint |
| account-personal-service | maxbank-transactions | personal accounts + types; nightly `PersonalAccountJob` (cashback via transaction-service) |
| account-business-service | maxbank-transactions | business accounts + types |
| transaction-service | maxbank-transactions | transaction orchestrator (see below), Tinkoff sandbox integration |
| payment-service | apelsin-payment | merchant orders / checkout; calls transaction-, account-business-, info-business-service |

### Transaction pipeline (RabbitMQ)

One topic exchange `exchange-main`. Queues and bindings are declared inline in `@RabbitListener(bindings = @QueueBinding(...))` — there are no `Queue`/`Binding` beans; `RabbitConfig` in each service only adds the JSON converter and the `errorTransactionHandler`, which republishes any listener failure to `transaction.cancel` (the rollback path, driven by `isWithdrawnMoney`/`isDepositedMoney` flags on `TransactionStatus`).

Routing keys, all in transaction-service unless noted: `transaction.open` → `commission` (inner sender) or `apply_payment` → `check_from` → fan-out `check_from.{business|personal}` (account services) → `withdraw` → `check_to` → `check_to.{business|personal}` → `deposit` → for `PAYMENT` type `hold` (payment-service listens, order → HOLD) until `unhold` → `close` → `close.after.{business|personal}`. `transaction.#` is logged to `transactions_history` by `LogTransactionListener`. payment-service publishes `transaction.unhold` / `transaction.cancel`.

`.uml/transaction_states.plantuml` and `payment_seq.plantuml` (gitignored but present locally) describe the same flow in business terms.

### Database

No Flyway/Liquibase at runtime: schema is `spring.jpa.hibernate.ddl-auto: update` everywhere. The Liquibase files under `auth-service/src/main/resources/changelog/` are orphaned (no dependency, targets a different auth server) — don't extend them. Entity changes in `account-starter` affect three services on one DB; keep them backward compatible.

## Gotchas

- Committed secrets: `.env`, `.keys/private.key`, SMTP password in users-service, Tinkoff sandbox token in `TinkoffApiConfig`. Treat them as already leaked; don't add more.
- `TransactionServiceImpl.createFromTinkoff` replaces the real Tinkoff pay URL with a `graduate.pshiblo.xyz/tinkoff/test` stub.
- `monitoring/prometheus.yml` scrapes only auth and transaction services; `grafana/` and `prometheus/` at the root are committed runtime data dirs mounted by the dev compose.
- `lombok.config` sets `addConstructorProperties=true` (needed for Jackson + `@AllArgsConstructor` DTOs).
