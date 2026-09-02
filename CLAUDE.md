# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

"Апельсин Pay" — backend of a demo bank / payment-gateway (graduation project, deployed at `graduate.pshiblo.xyz`). Spring Boot 2.7.18 + Spring Cloud 2021.0.9 + Spring Security 5.8 (overridden in the parent pom for Spring Authorization Server 0.4.5), **Java 11** (`maven.compiler.release=11`), Maven multi-module (`ru.pshiblo`). The React frontends live in a sibling repo `apelsin-pay-front`; the auth-service also embeds its own React login page. UI strings, commit messages and UML labels are in Russian.

## Build & run

There is no working Maven wrapper: `mvnw` is not executable and `.mvn/wrapper/` is gitignored, so use a system `mvn`. Target is Java 11; building on JDK 17 works (`maven.compiler.release=11`, Lombok 1.18.22), JDK 21 is untested.

```bash
mvn -q package -DskipTests                       # all modules, jars in */target
mvn -q package -DskipTests -pl transaction-service -am   # one service + the libs it needs
mvn test -pl auth-service,security-library,users-service -am   # the modules that have tests (auth: MockMvc OAuth2 flows)
mvn dependency:tree | grep spring-security-oauth2         # must show only SAS + spring-security-oauth2-{client,core,jose,resource-server}
```

Reactor order matters: `common-library` → `security-library` → `account-starter` / `feign-starter` → services. Always pass `-am` when building a single service.

Building `auth-service` runs `frontend-maven-plugin` (downloads Node 16 into `auth-service/src/main/resources/WEB-INF/view/react/node/`, runs `npm install`, `npm run lint:fix`, `npm run build`). It's slow and needs network; skip it with `-pl` when you're not touching auth, or pass `-Dskip.installnodenpm -Dskip.npm` (the jar then has no login page, fine for tests).

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

`config-service` is a Spring Cloud Config Server in `native` mode serving `config-service/src/main/resources/shared/*.yml`. **Ports, DB names, OAuth client registration (`spring.security.oauth2.client.registration.internal`), actuator, RabbitMQ and Eureka wiring for every service are there, not in the service modules.** `shared/application.yml` holds the docker/local profile split shared by all (token-uri, jwk-set-uri). When you add a service, add `shared/<app-name>.yml`, a `<NAME>_SERVICE_PASSWORD` env var and a `ServiceClient` entry in auth-service's `RegisteredClientsConfig`.

Ports: config 8888, registry 8761, gateway 8080, auth 5000, users 5001, transaction 6001, account-business 6002, account-personal 6003, info-personal 6004, info-business 6005, payment 6006.

### Request path

Frontend → `gateway-service` (Spring Cloud Gateway). There are **no explicit routes**: `gateway.yml` enables the discovery locator with `include-expression: serviceId.endsWith('-SERVICE')`, so every Eureka-registered `*-service` is exposed at `/<service-id>/**` with the prefix stripped (e.g. `/transaction-service/api/transaction/...`). auth-service is reachable both through the gateway and directly (nginx vhost `login.graduate.pshiblo.xyz`).

### Auth

`auth-service` is a **Spring Authorization Server 0.4.5** (`spring-security-oauth2-authorization-server`, Spring Security 5.8). Endpoints: `/oauth2/authorize`, `/oauth2/token`, `/oauth2/jwks`, `/.well-known/oauth-authorization-server`. Clients are in-memory in `RegisteredClientsConfig`:
- `browser_main` (scope `user`, main UI) and `browser_payment` (scope `user_payment`, payment page) — **public clients**: authorization_code + PKCE S256 (`ClientAuthenticationMethod.NONE`, `requireProofKey`, no consent screen). Redirect URIs come only from `URL_LOGIN_MAIN` / `URL_LOGIN_PAYMENT` (`auth.clients.*.redirect-uris`, plus `localhost:3000/3006` in the `local` profile). Access-token TTL per client: `auth.clients.*.access-token-ttl` in `shared/auth-service.yml`. SAS 0.4 does **not** issue refresh tokens to public clients, so the SPAs re-run the PKCE flow when the token expires.
- one `client_credentials` client per service, secret = `<NAME>_SERVICE_PASSWORD` env var (stored as a bcrypt hash, `client_secret_basic`/`client_secret_post`), scope `server` plus per-target scopes. The scope list must match `spring.security.oauth2.client.registration.internal.scope` of that service in config-service.

Tokens are RSA JWTs. Keys come from `$AUTH_KEYS_PATH/{private,public}.key` (default `./.keys`, Base64 DER); if missing, `RsaKeyPairStore` generates a pair. `.keys/` is gitignored — the old committed pair was treated as compromised. Nothing mounts it in compose, so a restarted auth container gets a new key and invalidates all tokens; mount a volume if that matters. Resource servers fetch the JWK Set from `/oauth2/jwks` (`spring.security.oauth2.resourceserver.jwt.jwk-set-uri` in `shared/application.yml`). `ApelsinTokenCustomizer` adds claims `client_id` (all tokens) and `user_name`, `id`, `email`, `name`, `status`, `lock`, `authorities` (user tokens); `scope` is a JSON array. Issuer is `auth.issuer` (`AUTH_ISSUER`, default `http://login.graduate.pshiblo.xyz` in docker).

Login page: the embedded React app at `/index.html` posts to `/perform_login` (formLogin in `SecurityConfig`); unauthenticated `/oauth2/authorize` requests are redirected there and back. `auth-service` has no DB (JPA was removed): `UserServiceImpl` builds the principal by Feign-calling `users-service` (credentials) and `info-personal-service` (status/lock).

Passwords: one `PasswordEncoder` for auth-service and users-service, auto-configured by `common-library` (`ApelsinPasswordEncoders`) when `encoder.sha512.salt` (`AUTH_SHA_SALT`) is set — a `DelegatingPasswordEncoder` writing `{bcrypt}` and matching un-prefixed legacy HmacSHA512 hashes. On a successful login with a legacy hash `UserPasswordUpgradeService` (`UserDetailsPasswordService`) stores the bcrypt hash via `PUT users-service/internal/users/{id}/password-hash` (scope `server` only). Keep `AUTH_SHA_SALT` until `SELECT count(*) FROM users WHERE password_hash NOT LIKE '{bcrypt}%'` is 0.

`security-library` (auto-configured in every business service via `spring.factories`) is the resource-server side (`SecurityFilterChain` bean): CORS on, CSRF off, `/actuator/**`, `/public/**`, `/error/**` open, **everything else requires `ROLE_UNBAN`**. `CustomJwtGrantedAuthoritiesConverter` grants `ROLE_UNBAN` when claim `lock=false` or scope `server` — that's the ban mechanism. Use `@IsAdmin` / `@IsModer` / `@IsUser` and `AuthUtils.getAuthUser()`.

### Shared libraries

- `common-library` — password encoders (`ru.pshiblo.common.security`, see Auth), `ApelsinException` hierarchy (NotFound 404, AlreadyExist 400, Security/NotAllowedOperation 403, Internal 500) and `ExceptionControllerAdvice` (auto-configured) that maps them plus validation errors to a `{status,message,violations[]}` body. `RequestMapper`/`ResponseMapper`/`CommonMapper` are the interfaces every MapStruct mapper implements.
- `feign-starter` — `@EnableFeignClients`, `spring-boot-starter-oauth2-client` + an `OAuth2AuthorizedClientManager` (client_credentials) and Spring Cloud OpenFeign's `OAuth2AccessTokenInterceptor` (wrapped in `InternalOAuth2RequestInterceptor`, which skips requests that already carry `Authorization`, e.g. `TinkoffApi`) attaching a token from registration `internal` (`spring.security.oauth2.client.registration.internal` + `provider.apelsin.token-uri`, set per service in config-service) to every Feign call, and `CustomErrorDecoder` turning non-2xx into `IntegrationException(status, body)`, which `ExceptionControllerAdvice` re-emits with the original status. This is how errors propagate across services. Used by all services including `transaction-service` and `auth-service`.
- `account-starter` — the shared account domain (`Account`, `HoldMoney` entities, `AccountService`, `Currency`, `CurrencyServiceImpl` pulling CBR rates daily at 08:00). Embedded in transaction-service and both account services, which is why they share the `maxbank-transactions` DB and the `accounts`/`hold_money` tables.

### Services

| service | DB | role |
|---|---|---|
| users-service | maxbank-users | logins, password hashes (`{bcrypt}` or legacy Hmac), roles, password-reset emails (SMTP creds in its `application.yml`), `internal/**` endpoints for services |
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

No Flyway/Liquibase at runtime: schema is `spring.jpa.hibernate.ddl-auto: update` everywhere. The Liquibase files under `auth-service/src/main/resources/changelog/` are orphaned (no dependency, auth-service has no DB at all) — don't extend them. Entity changes in `account-starter` affect three services on one DB; keep them backward compatible.

## Gotchas

- Committed secrets: `.env`, SMTP password in users-service, Tinkoff sandbox token in `TinkoffApiConfig`. Treat them as already leaked; don't add more. The JWT signing key is no longer in git (`.keys/` ignored).
- `TransactionServiceImpl.createFromTinkoff` replaces the real Tinkoff pay URL with a `graduate.pshiblo.xyz/tinkoff/test` stub.
- `monitoring/prometheus.yml` scrapes only auth and transaction services; `grafana/` and `prometheus/` at the root are committed runtime data dirs mounted by the dev compose.
- `lombok.config` sets `addConstructorProperties=true` (needed for Jackson + `@AllArgsConstructor` DTOs).
