# AGENTS.md

This file provides guidance to AI agents when working with code in this repository.

## Prerequisites

- **JDK**: 21.0.3 (the build targets Java 21; Spring Boot 4 requires Java 17+)
- **Maven**: 3.9.6
- **PostgreSQL**: 16.0 (schema: `ida`)
- **Keycloak**: Required for auth token issuance

## Build Commands

```bash
# Build all authentication modules (from repo root)
cd authentication
mvn clean install -Dmaven.javadoc.skip=true -Dgpg.skip=true

# Build skipping tests
mvn install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true

# Build a single service (e.g., authentication-service)
cd authentication/authentication-service
mvn install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true

# Build Docker image for a service
cd authentication/<service-directory>
docker build -f Dockerfile .
```

## Testing

```bash
# Run all unit tests
cd authentication
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Run with SonarQube analysis
mvn install -Psonar

# Build API test suite
cd api-test
mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true

# Execute API tests via JAR
cd api-test/target
java -jar -Dmodules=auth \
  -Denv.user=api-internal.<env_name> \
  -Denv.endpoint=<base_env> \
  -Denv.testLevel=smokeAndRegression \
  apitest-auth-1.2.1-jar-with-dependencies.jar
```

API test levels: `smoke` (positive only) or `smokeAndRegression` (all).  
Before running API tests, update credentials in `api-test/src/main/resources/config/Ida.properties`.

## Deployment

```bash
# Kubernetes (requires KUBECONFIG set)
cd deploy && ./install.sh
cd deploy && ./delete.sh
cd deploy && ./restart.sh

# Local Docker Compose (PostgreSQL + Config Server + WireMock + BioSDK)
cd authentication/local-dev-setup/docker-compose
docker-compose up -d
```

## Architecture

This is a **Spring Boot 4.1.1 multi-module Maven project** (`authentication-parent`, group `io.mosip.authentication`). As of Issue #1764 it exposes a single consolidated REST service — `authentication-service` — for identity authentication within the MOSIP platform; the formerly separate `authentication-internal-service` and `authentication-otp-service` modules were merged into it (see `IdAuthenticationApplication`) and removed.

**Platform versions** (Boot 4 upgrade): the root `pom.xml` uses `spring-boot-starter-parent` 4.1.1 (the `kernel-bom` is no longer imported), with Spring Framework 7, Spring Cloud 2025.1.3, Spring Batch 6, Hibernate 7, Spring Security 7 and springdoc 3.x. MOSIP kernel/idrepository libraries are `1.4.1-SNAPSHOT` (`kernel-core` now bundles several formerly separate kernel libraries and auto-configures them; unused ones are excluded in `IdAuthenticationApplication`).

Things that are easy to break when touching dependencies or tests:
- **Jackson 2 is kept** (`com.fasterxml.jackson`, pinned via `jackson-bom` 2.22.3 in the root pom) because the codebase and kernel libs use it; Boot 4 defaults to Jackson 3 (`tools.jackson`), so also set `spring.http.converters.preferred-json-mapper=jackson2` in the config.
- **JAXB is pinned to the javax-era 2.3.9 runtime** (`org.glassfish.jaxb:jaxb-runtime` and `txw2` in the root `dependencyManagement`). `kernel-biometrics-api` (`CbeffValidator`, BIR/CBEFF) still uses `javax.xml.bind`; the Boot 4 BOM would otherwise replace the runtime with the Jakarta 4.x one and bio auth fails with "Implementation of JAXB-API has not been found". Remove the pin only after the kernel moves CBEFF to `jakarta.xml.bind`.
- **Tests are JUnit 5 + Mockito 5 only.** PowerMock and the JUnit 4 runners were removed; do not reintroduce them. Spring slice tests use `spring-boot-starter-webmvc-test`. The root pom sets `spring.cloud.config.enabled=false` and `spring.cloud.config.import-check.enabled=false` for surefire so unit tests never contact a config server.
- **Spring Batch 6 is JDBC-backed** (`spring-boot-starter-batch-jdbc`); job metadata is stored in the `ida.batch_*` tables. Batch 6 renamed the sequence `BATCH_JOB_SEQ` to `BATCH_JOB_INSTANCE_SEQ` (see `db_upgrade_scripts/mosip_ida/sql/1.3.1_to_1.4.0_upgrade.sql` and `db_scripts`). A `JobRegistry` bean is provided by `BatchJobRegistryConfig`.
- **Dates:** `IdaDateUtil.parseToDate` is a stopgap for date-only patterns, because `kernel-core`'s `DateUtils2.parseToDate` rejects them. Remove it once `kernel-core` is fixed.
- **Resource filtering:** the Boot parent filters only `application*.{properties,yml,yaml}` using the `@...@` delimiter (not `${...}`). Avoid unrelated `@` pairs in values of such files.

### Module Layout

```
authentication/               # Parent Maven module
  authentication-core/        # Core SPI and biometric matching algorithms
  authentication-filter-api/  # Filter chain interfaces
  authentication-common/      # Shared entities, repositories, utilities
  authentication-authtypelockfilter-impl/  # Blocks auth when type is locked
  authentication-hotlistfilter-impl/       # Blocks blacklisted IDs/devices
  authentication-service/     # Consolidated service: public + internal + OTP (port 8090)
  esignet-integration-impl/   # eSignet digital signature integration
  local-dev-setup/            # Docker Compose for local dev
api-test/                     # TestNG + REST Assured API test rig
deploy/                       # Helm charts and K8s install scripts
db_scripts/                   # PostgreSQL DDL scripts
```

### One Consolidated Service, Two Consumer Classes

| App | Context Path | Port | Consumer |
|---|---|---|---|
| `authentication-service` | `/idauthentication/v1` | 8090 | Both external Auth/KYC Partners **and** internal MOSIP modules (Reg Processor, Resident) — same pod, same port |

The internal-only controllers (`InternalAuthController`, `InternalOTPController`, `InternalAuthTxnController`,
`InternalUpdateAuthTypeController`, ...) have **no** `/internal` path segment of their own — they are
plain paths under the shared `/idauthentication/v1` context path (e.g. `POST /idauthentication/v1/auth`,
`GET /idauthentication/v1/authTransactions/individualId/{ID}`), distinguished from the public partner
routes only by not having the `{MISP-LK}/{Auth-Partner-ID}/{API-Key}` path variables.
`helm/ida-auth/values.yaml` (`istio.match`) carries the old `/idauthentication/v1/internal` and
`/idauthentication/v1/otp` prefixes forward from the removed `ida-internal`/`ida-otp` charts, still
restricted to `gateways: [istio-system/internal]` as they were on their original standalone charts
(neither was ever on the public gateway). Note `/idauthentication/v1/internal` no longer matches any
real route post-merge — the internal controllers dropped that path segment and now listen on plain
paths like `/auth`, `/otp`, `/authTransactions` directly under `/idauthentication/v1` (see above);
those bare paths are covered by the public `/idauthentication/v1/auth` prefix entry, not the
internal-only one, so this consolidation preserves the old chart-level gateway split but does not by
itself make the merge's internal-only bare-path routes internal-gateway-only.

Key endpoints:
- `POST /idauthentication/v1/auth/{MISP-LicenseKey}/{Auth-Partner-ID}/{Partner-Api-Key}` — public: authenticate via UIN/VID
- `POST /idauthentication/v1/kyc/{...}` — public: E-KYC request
- `POST /idauthentication/v1/otp/{MISP-LicenseKey}/{Auth-Partner-ID}/{Partner-Api-Key}` — public: OTP generation
- `POST /idauthentication/v1/auth` — internal-only: internal authentication (bare path)
- `POST /idauthentication/v1/otp` — internal-only: internal OTP request (bare path)
- `GET /idauthentication/v1/authTransactions/individualId/{ID}` — internal-only: auth transaction history

### Authentication Types

Any combination of: **OTP**, **Demographic** (name, DOB, gender), **Biometric** (Fingerprint/FMR, IRIS, Face).

### Filter Chain

Authentication requests pass through an ordered filter chain configured by:
- `ida.mosip.external.auth.filter.classes.in.execution.order`
- `ida.mosip.internal.auth.filter.classes.in.execution.order`

Key filter implementations: `authentication-authtypelockfilter-impl` and `authentication-hotlistfilter-impl`.

### External Dependencies

- **Keycloak** — auth token issuance for service-to-service calls
- **Bio-SDK HTTP Service** — biometric matching
- **HSM** — cryptographic key management (via `kernel-keymanager-service`)
- **WebSub** — event subscriptions for credential, identity, partner, master data, hotlist updates
- **ID Repository** — identity data storage
- **Partner Management Service** — MISP/partner validation

### Configuration

All property files are externalized to [mosip-config](https://github.com/mosip/mosip-config/tree/master) and loaded via a Spring Cloud Config Server at startup. Key files:
- `id-authentication-default.properties` — common IDA config
- `id-authentication-external-default.properties` — external service config
- `id-authentication-internal-default.properties` — internal service config
- `id-authentication-otp-default.properties` — OTP service config
- `application-default.properties` — platform-wide config

Settings for Spring Boot 4 (config repo):
```properties
# required: keep Jackson 2 for HTTP message conversion (Boot 4 defaults to Jackson 3)
spring.http.converters.preferred-json-mapper=jackson2
```
Boot 3-era settings that must NOT be carried over: `spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER` (Spring MVC now uses `PathPatternParser`; remove it) and `hibernate.dialect` (Hibernate 7 selects `PostgreSQLDialect` itself and logs HHH90000025 if it is set). Renamed keys: `spring.datasource.initialization-mode` -> `spring.sql.init.mode`, `management.metrics.export.prometheus.enabled` -> `management.prometheus.metrics.export.enabled`; `server.max-http-header-size` is no longer supported, so the 8KB default applies unless `server.max-http-request-header-size` is set (e.g. `10000000` to keep 10MB); `management.endpoint.<id>.enabled` is replaced by `management.endpoint.<id>.access`.

`bootstrap.properties` ships a placeholder `spring.cloud.config.uri=localhost`; for local runs override it (e.g. `--spring.cloud.config.uri=http://localhost:51000/config`) and add `--spring.cloud.loadbalancer.enabled=false` so `@LoadBalanced` clients treat `localhost` as a host, not a service name.

### Container start

`authentication-service/Dockerfile` ends with `CMD exec java ... $spring_args <module>.jar`. `configure_start.sh` (the `ENTRYPOINT`) installs the HSM client, downloads the IAM adapter (`kernel-auth-adapter.jar` from `$iam_adapter_url_env` into `$loader_path_env`, picked up through `-Dloader.path`) and exports `spring_args` (`-Dspring.cloud.config.name=...` when `spring_config_name_env` is set) before `exec "$@"`. The IAM adapter is not bundled in the jar; local runs of the fat jar need it on `-Dloader.path`.

### CI/CD

GitHub Actions (`.github/workflows/push-trigger.yml`) triggers on push to `release*`, `master`, `develop*`, `1.*`, `MOSIP*` branches. Pipeline: build → Nexus publish → Docker image builds (3 parallel) → SonarCloud analysis → API test build.

### Swagger

When running locally:
`http://localhost:8080/idauthentication/v1/swagger-ui/index.html?configUrl=/idauthentication/v1/v3/api-docs/swagger-config`