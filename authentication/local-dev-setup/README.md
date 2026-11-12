# IDA Local Development Setup Guide

This guide walks you through setting up the ID Authentication (IDA) service locally **without** standing up the full MOSIP platform. All external MOSIP dependencies (database, config server, master-data, biometric SDK) are replaced with lightweight Docker containers provided in the `local-dev-setup/docker-compose/` directory.

---

## Prerequisites

Install the following tools before proceeding:

| Tool | Version |
|------|---------|
| Git | Latest |
| Java | 21 |
| Maven | 3.9.6 |
| Docker + Docker Compose | Latest |

> **HSM note:** This setup uses a **PKCS12 keystore file** (`mosip-ida-ks.p12`) instead of SoftHSM. No external HSM installation is required.

---

## 1. Clone the Repositories

Clone both repositories from the MOSIP GitHub organisation. Use the `develop` branch for both.

```bash
git clone -b develop https://github.com/mosip/mosip-config
git clone -b develop https://github.com/mosip/id-authentication
```

---

## 2. Build the Project

From the root of the `id-authentication` repository, build all modules. The `-Dgpg.skip=true` flag skips GPG signing, which is not required in a local environment.

```bash
cd id-authentication/authentication
mvn clean install -Dgpg.skip=true
```

---

## 3. Configure `mosip-config`

Navigate to your cloned `mosip-config` folder and edit the following property files.

### 3.1 `application-default.properties`

Update these four URLs to point to the local WireMock mock service (port `8082`):

| Property | Old Value | New Value |
|----------|-----------|-----------|
| `mosip.kernel.masterdata.url` | `http://masterdata.kernel` | `http://localhost:8082` |
| `mosip.kernel.notification.url` | `http://notifier.kernel` | `http://localhost:8082` |
| `mosip.kernel.otpmanager.url` | `http://otpmanager.kernel` | `http://localhost:8082` |
| `mosip.websub.url` | `http://websub.websub` | `http://localhost:8082` |

---

### 3.2 `id-authentication-default.properties`

#### Add these new properties

```properties
mosip.api.public.host=localhost
mosip.mock.biosdk.url=http://localhost:8083
mosip.esignet.host=localhost
spring.http.converters.preferred-json-mapper=jackson2
```

> `spring.http.converters.preferred-json-mapper=jackson2` is required on Spring Boot 4 to keep Jackson 2 for HTTP message conversion. Do **not** carry over the Boot 3-era `spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER` or `hibernate.dialect` properties — remove them if present.

#### Change these existing properties

| Property | Old Value | New Value |
|----------|-----------|-----------|
| `mosip.ida.auth.secretKey` | `${mpartner.default.auth.secret}` | `a` |
| `mosip.ida.database.hostname` | `${mosip.database.hostname.override:postgres-postgresql.postgres}` | `localhost` |
| `mosip.ida.database.port` | `${mosip.database.port.override:5432}` | `5455` |
| `mosip.ida.database.user` | `idauser` | `postgres` |
| `mosip.ida.database.password` | `${db.dbuser.password}` | `mosip123` |
| `ida-websub-authtype-callback-secret` | `${ida.websub.authtype.callback.secret}` | `a` |
| `ida-websub-credential-issue-callback-secret` | `${ida.websub.credential.issue.callback.secret}` | `a` |
| `ida-websub-partner-service-callback-secret` | `${ida.websub.partner.service.callback.secret}` | `a` |
| `ida-websub-hotlist-callback-secret` | `${ida.websub.hotlist.callback.secret}` | `a` |
| `ida-websub-masterdata-templates-callback-secret` | `${ida.websub.masterdata.templates.callback.secret}` | `a` |
| `ida-websub-masterdata-titles-callback-secret` | `${ida.websub.masterdata.titles.callback.secret}` | `a` |
| `ida-websub-credential-issue-callback-url` | *(empty)* | `a` |
| `mosip.kernel.keymanager.hsm.config-path` | `/config/softhsm-application.conf` | `<absolute-path-to>/local-dev-setup/mosip-ida-ks.p12` |
| `mosip.kernel.keymanager.hsm.keystore-type` | `PKCS11` | `PKCS12` |
| `mosip.kernel.keymanager.hsm.keystore-pass` | `${softhsm.ida.security.pin}` | `qwerty@1234` |
| `mosip.kernel.tokenid.uin.salt` | `${mosip.kernel.uin.salt}` | `zHuDEAbmbxiUbUShgy6pwUhKh9DE0EZn9kQDKPPKbWscGajMwf` |
| `mosip.kernel.tokenid.partnercode.salt` | `${mosip.kernel.partnercode.salt}` | `yS8w5Wb6vhIKdf1msi4LYTJks7mqkbmITk2O63Iq8h0bkRlD0d` |
| `mosip.ida.allowed.domain.uris` | *(existing value)* | append `,localhost` to the existing value |
| `ida-default-identity-filter-attributes` | `phone,fullName,dateOfBirth,email` | `phone,firstName,middleName,lastName,dateOfBirth,email` |
| `mosip.ida.kyc.token.secret` | `${mosip.ida.kyc.token.secret}` | `j91eRPq0n4zbOZFQ6uBgWMwV7lnV8_2mpRw_sozLSdQ` |

> **Keystore path:** Replace `<absolute-path-to>` with the absolute path to the `local-dev-setup` folder on your machine.  
> Example: `/home/dev/id-authentication/authentication/local-dev-setup/mosip-ida-ks.p12`

---

### 3.3 `id-authentication-external-default.properties`

| Property | Old Value | New Value |
|----------|-----------|-----------|
| `ida-websub-ca-certificate-callback-secret` | `${ida.websub.ca.certificate.callback.secret}` | `a` |

---

### 3.4 `id-authentication-otp-default.properties`

> The OTP service has been merged into `authentication-service`; apply this only if your config repo still loads this file.

| Property | Old Value | New Value |
|----------|-----------|-----------|
| `ida-websub-ca-certificate-callback-secret` | `${ida.websub.ca.certificate.callback.secret}` | `a` |

---

### 3.5 `identity-mapping.json`

| Field | Old Value | New Value |
|-------|-----------|-----------|
| `identity.name.value` | `fullName` | `firstName,middleName,lastName` |

---

## 4. Start Docker Services

The `local-dev-setup/docker-compose/` directory contains a Docker Compose file that starts the following services:

| Service | Port | Description |
|---------|------|-------------|
| PostgreSQL | `5455` | IDA database (pre-seeded via `init.sql`) |
| Config Server | `51000` | Spring Cloud Config Server serving `mosip-config` |
| WireMock (mock-service) | `8082` | Mocks master-data, notification, OTP manager, and WebSub APIs |
| BioSDK Service | `8083` | Mock biometric SDK server |

### Prerequisite: prepare the BioSDK mock library

The `biosdk-service` downloads a ZIP file containing the mock BioSDK JAR on startup. This file is not stored in the repository (too large for git). Run the provided script **once** before starting Docker services to download the JAR from Maven Central and package it:

**macOS / Linux:**
```bash
cd local-dev-setup
chmod +x prepare-biosdk-mock-lib.sh
./prepare-biosdk-mock-lib.sh
```

**Windows:**
```cmd
cd local-dev-setup
prepare-biosdk-mock-lib.bat
```

The script downloads `mock-sdk-1.3.0-beta.1-jar-with-dependencies.jar` (~110 MB) from Maven Central, zips it as `mock-sdk.zip`, and places it in `docker-compose/wiremock/__files/`. You only need to run it once; skip it on subsequent starts unless the file is deleted.

### Configure the Config Server volume

Before starting, open `docker-compose/docker-compose.yml` and update the config-server volume mount to the **absolute path** of your cloned `mosip-config` directory:

```yaml
volumes:
  - /absolute/path/to/mosip-config:/mosip-config
```

### Start the services

```bash
cd local-dev-setup/docker-compose
docker compose up -d
```

### Verify all services are running

```bash
docker compose ps
```

All four services (`database`, `config-server`, `mock-service`, `biosdk-service`) should show status `running` or `healthy`.

---

## 5. Start the Authentication Service

`authentication-service` is the single consolidated service (public auth/KYC/OTP APIs and the internal APIs). The former `authentication-otp-service` and `authentication-internal-service` no longer exist, so there is only one service to start.

### 5.1 Add the `kernel-auth-adapter` dependency

The authentication service requires `kernel-auth-adapter` as a runtime dependency. It is not bundled in the fat jar: for `mvn spring-boot:run` / IDE runs add the dependency below, and for `java -jar` runs also put the adapter jar on `-Dloader.path`. Open `authentication-service/pom.xml` and add the following inside the `<dependencies>` block:

```xml
<dependency>
    <groupId>io.mosip.kernel</groupId>
    <artifactId>kernel-auth-adapter</artifactId>
    <version>${kernel-auth-adapter.version}</version>
</dependency>
```

> The version property `${kernel-auth-adapter.version}` is already defined in the parent `pom.xml`. Do **not** hard-code the version.

### 5.2 Update `bootstrap.properties`

Open `authentication-service/src/main/resources/bootstrap.properties` and apply the following changes:

| Property | Old Value | New Value |
|----------|-----------|-----------|
| `spring.profiles.active` | `dev` | `default` |
| `spring.cloud.config.label` | `mz` | `master` |
| `spring.cloud.config.uri` | `localhost` | `http://localhost:51000/config` |

Also **add** these four properties (the committed `spring.cloud.config.uri=localhost` is only a placeholder, so the override is mandatory; alternatively pass them as CLI args, e.g. `--spring.cloud.config.uri=http://localhost:51000/config`):

```properties
mosip.api.internal.host=http://localhost:8090
keycloak.external.url=http://localhost:8082
keycloak.internal.url=http://localhost:8082
spring.cloud.loadbalancer.enabled=false
```

> `spring.cloud.loadbalancer.enabled=false` disables Spring Cloud LoadBalancer so that `@LoadBalanced` clients call `localhost` URLs directly instead of treating `localhost` as a service name (which causes a 503 at startup when the master-data cache is initialised).

### 5.3 Run the Authentication Service

#### Option A — Maven

```bash
cd authentication/authentication-service
mvn spring-boot:run
```

#### Option B — JAR

Since `bootstrap.properties` was modified after the initial build, rebuild the module first to include the updated configuration in the JAR:

```bash
cd authentication/authentication-service
mvn clean install -Dgpg.skip=true
java -Dloader.path=<dir-containing-kernel-auth-adapter.jar> -jar target/authentication-service-*.jar
```

#### Option C — IntelliJ IDEA

1. Open the `id-authentication/authentication` directory as a Maven project (**File → Open**).
2. Wait for IntelliJ to finish importing and indexing all modules.
3. Navigate to `authentication-service/src/main/java` and open the main application class (`IdAuthenticationApplication.java`).
4. Click the **Run** button (green triangle) next to the class declaration, or right-click the file and choose **Run 'IdAuthenticationApplication'**.
5. To persist the run configuration, open **Run → Edit Configurations**, select the generated configuration, and verify the **Working directory** is set to the `authentication-service` module root.

#### Option D — VS Code

1. Install the **Extension Pack for Java** (`vscjava.vscode-java-pack`) if not already installed.
2. Open the `id-authentication/authentication` folder (**File → Open Folder**).
3. Wait for the Java Language Server to finish building the workspace.
4. Open `authentication-service/src/main/java/.../IdAuthenticationApplication.java`.
5. Click **Run** above the `main` method (shown by the CodeLens link), or press `F5` with the file open.
6. VS Code will auto-generate a launch configuration in `.vscode/launch.json`; you can edit it to add any `-D` JVM arguments if needed.

The service starts on port **8090** with context path `/idauthentication/v1`.

Swagger UI is available at:
```
http://localhost:8090/idauthentication/v1/swagger-ui/index.html
```

---

## 6. IDA Sample Client (CLI)

The `IDA-sample-client` is an interactive Python CLI for testing the OTP, Authentication, KYC and key-binding APIs (all served by the single `authentication-service`) without needing a full MOSIP platform or a UI. It handles request encryption, signing, and all API communication.

### Folder Structure

```
IDA-sample-client/
├── client/
│   ├── requirements.txt
│   └── authenticator/
│       ├── main.py                          # Entry point
│       ├── authenticator.py                 # Core auth logic
│       ├── cli_options.py                   # Interactive menu
│       ├── authenticator-config-local.toml  # Configuration
│       ├── model/
│       │   └── auth_request.py              # Request models
│       ├── utils/
│       │   ├── cryptoutil.py                # Encryption & signing
│       │   └── restutil.py                  # HTTP calls
│       └── exceptions/
│           └── authenticator_exception.py
├── new-keys/
│   ├── ida-partner-local.cer                # IDA encryption certificate
│   └── partner-mosip-signed.p12             # Partner signing keystore
└── samples/
    └── auth-req-*.json                      # Sample auth payloads (UIN/VID, VID variants, infant, ...)
```

### Prerequisites

- Python **3.10** or later (the code uses `list[...]` and `X | Y` type hints which require 3.10+)

Verify your Python version:

```bash
python3 --version
```

### 6.1 Review the Configuration

Open `IDA-sample-client/client/authenticator/authenticator-config-local.toml`. The defaults are pre-configured for the local Docker setup — no changes are needed unless you have customised ports or partner credentials.

Key settings to be aware of:

| Setting | Default | Description |
|---------|---------|-------------|
| `ida_auth_url` | `http://localhost:8090/idauthentication/v1/auth` | Authentication service endpoint |
| `ida_otp_url` | `http://localhost:8090/idauthentication/v1/otp` | OTP endpoint (same service as auth) |
| `ida_kyc_auth_v2_url` / `ida_kyc_exchange_v2_url` | `http://localhost:8090/idauthentication/v1/kyc-auth/v2/delegated` / `.../kyc-exchange/v2/delegated` | KYC v2 endpoints (also `ida_ekyc_url`, `ida_kyc_auth_url`, `ida_kyc_exchange_url`, `ida_vci_exchange_url`, `ida_key_binding_url`) |
| `mock_mds_capture_url` | `http://localhost:4501/capture` | Mock MDS capture endpoint used for biometric auth |
| `bound_keys_dir` | `new-keys/bound` | Where key-binding `.p12` files are stored (deleted on clean exit) |
| `encrypt_cert_path` | `new-keys/ida-partner-local.cer` | Certificate used to encrypt the auth request (relative to `IDA-sample-client/`) |
| `sign_p12_file_path` | `new-keys/partner-mosip-signed.p12` | Keystore used to sign the request (relative to `IDA-sample-client/`) |
| `sign_p12_file_password` | `1234` | Password for the signing keystore |
| `partner_id` | `auth_v1z0816154432` | Partner identifier |
| `partner_apikey` | `198833` | Partner API key |
| `partner_misp_lk` | `vM1BA8RosNt3kemNGpsneYgcelZwNmsOoKJQgMm92DltqtJh4p` | MISP licence key |

### 6.2 Set Up a Python Virtual Environment

All commands below must be run from the `IDA-sample-client/` directory. The key file paths in the config are relative to this directory.

```bash
cd local-dev-setup/IDA-sample-client
```

Create and activate a virtual environment:

```bash
# Create the venv
python3 -m venv .venv

# Activate — macOS / Linux
source .venv/bin/activate

# Activate — Windows
.venv\Scripts\activate
```

Your prompt will change to show `(.venv)` when the environment is active.

### 6.3 Install Dependencies

```bash
pip install -r client/requirements.txt
```

> **Python 3.12+ / 3.14 note:** `dynaconf==3.1.9` (the original pinned version) depends on `pkg_resources` which is no longer bundled with Python 3.12+. The `requirements.txt` has been updated to `dynaconf==3.2.4` which resolves this. If you see `ModuleNotFoundError: No module named 'pkg_resources'`, run `pip install "dynaconf==3.2.4"` to fix it.

### 6.4 Run the Client

Make sure you are still in the `IDA-sample-client/` directory and the venv is active, then run:

```bash
python client/authenticator/main.py
```

You will see the configured IDA URL printed, followed by the main menu:

```
Main Menu:
1. Auth (True/False)
2. eKYC
3. kyc-auth & kyc-exchange/vci-exchange (v1)
4. kyc-auth & kyc-exchange (v2)
5. Identity key binding
0. Exit
```

Each category opens a sub-menu with the auth factors it supports (`0` returns to the main menu).

### 6.5 Menu Options

| Main menu | Sub-menu actions |
|-----------|------------------|
| 1. Auth (True/False) | Demo auth, Send OTP request, OTP auth, Demo + OTP, Biometric auth |
| 2. eKYC | Same auth factors as above, followed by a KYC request |
| 3. kyc-auth & kyc-exchange / vci-exchange (v1) | Auth factors, then `kyc-auth`, `kyc-exchange` and `vci-exchange` |
| 4. kyc-auth & kyc-exchange (v2) | Auth factors including **Password** and **Key binded token**, then `kyc-auth` v2 and `kyc-exchange` v2 (returns verified-claims metadata when available) |
| 5. Identity key binding | Binds a key to the identity; the key + certificate are stored as a `.p12` under `new-keys/bound/` (deleted on a clean exit) |

#### Demo Auth

Performs a demographic authentication using a sample JSON file.

1. Select a sample file from the `samples/` directory (e.g. `auth-req-01.json`).
2. The client encrypts and signs the request, then sends it to the auth service.
3. The response prints `authStatus: true/false` and any errors.

The sample file format (`samples/auth-req-01.json`):
```json
{
  "vid": "<VID or UIN>",
  "name": [{"language": "eng", "value": "John David Smith"}],
  "gender": [{"language": "eng", "value": "Male"}],
  "phoneNumber": "9912993478",
  "emailId": "john.smith@example.com",
  "fullAddress": [{"language": "eng", "value": "123 Main Street"}]
}
```

> **Note:** UIN/VID values are stored hashed in the database, not in plain text. Use only the UIN/VIDs listed in [Available Identities](#available-identities-uinvid). Changing the value to an arbitrary one will result in an authentication failure because the corresponding hash will not be found in the database.

---

#### Send OTP Request

Sends an OTP to the registered mobile/email for a given UIN/VID.

1. Enter a UIN or VID when prompted.
2. The service returns a `transactionID` — **copy this**, you will need it for OTP auth and Demo + OTP.
3. The actual OTP is not delivered (no real notifier is running). Read it from the Docker Compose logs by grepping for the generation event:

```bash
docker compose logs -f mock-service | grep --line-buffered "GENERATION_SUCCESSFUL"
```

The matching log line will contain the generated OTP value.

---

#### OTP Auth

Authenticates using an OTP received from *Send OTP Request*.

1. Enter the UIN/VID.
2. Enter the OTP from the Docker logs.
3. Enter the `transactionID` returned by *Send OTP Request*.

---

#### Demo + OTP Auth

Combines demographic data and an OTP in a single auth request.

1. Enter the UIN/VID.
2. Enter the OTP from the Docker logs.
3. Enter the `transactionID` from *Send OTP Request*.
4. Select a sample demographic file from the `samples/` directory.

---

#### Biometric Auth

Authenticates using a biometric captured from the **Mock MDS** (see [Section 7](#7-mock-mds-biometric-capture)).

1. Make sure Mock MDS is running.
2. Enter the UIN/VID.
3. Pick a biometric from the list (Face, individual Left/Right fingers, Left/Right Iris).

---

#### Password Auth (kyc-auth v2)

Available under **4. kyc-auth & kyc-exchange (v2)**. Enter the UIN/VID and the password (input is hidden). See the password identities in [Available Identities](#available-identities-uinvid).

---

#### Key Binded Token Auth (kyc-auth v2)

Available under **4**. Uses the key + certificate stored by *5. Identity key binding* for the same UIN/VID, so run key binding first.

---

### Available Identities (UIN/VID)

The following identities are available in the local database. Use any of the UIN/VIDs below for OTP, demographic, biometric and KYC requests.

| UIN | Mapped VID(s) | Notes |
|-----|---------------|-------|
| `8952059168` | – | Default identity used by `samples/auth-req-01.json` |
| `8497103958` | `8971487034965918` | |
| `8941269276` | `8708548136415038`, `8948612871402917` | |
| `8918362358` | `8216925367201389` | |
| `8148718920` | N/A | |
| `8724928329` | `8564539580423608` | |
| `8375416927` | `8526937256046298` | |
| `8354720784` | `8420394176306134`, `8021750691607687` | |
| `8130162368` | `8694304726328408` | Password identity — password: `Password@123` |
| `8296342506` | `8421783185386241` | Password identity + Verifiable Claims — password: `Password$567` |

Several `samples/auth-req-*.json` files already reference these (e.g. `auth-req-05-vid.json` → VID `8971487034965918`, `auth-req-10-infant.json` → VID `8526937256046298`).

---

### 6.6 Deactivate the Virtual Environment

When done, deactivate the venv:

```bash
deactivate
```


---

## 7. Mock MDS (Biometric Capture)

Biometric auth from the sample client needs a Mock MDS (Mosip Device Service) that returns captured biometrics. The sample client calls it at `mock_mds_capture_url` (`http://localhost:4501/capture`).

### 7.1 Check out the code

Use the `develop` branch of [mosip-mock-services](https://github.com/mosip/mosip-mock-services/tree/develop/MockMDS):

```bash
git clone -b develop https://github.com/mosip/mosip-mock-services
cd mosip-mock-services/MockMDS
mvn clean install -Dgpg.skip=true -DskipTests
```

### 7.2 Properties to update

Add/update the following properties in the MockMDS configuration (or pass them as `-D` Java arguments, see below).

Keystores for face and iris:

```properties
mosip.mock.sbi.file.face.keys.keystorefilename.ftm=/Biometric Devices/Finger/Single/Keys/mosipfingersingleftm-ida-local.p12
mosip.mock.sbi.file.face.keys.keyalias.ftm=mosipfingersingleftm
mosip.mock.sbi.file.face.keys.keystorepwd.ftm=mosipfingersingleftm

mosip.mock.sbi.file.iris.single.keys.keystorefilename.ftm=/Biometric Devices/Finger/Single/Keys/mosipfingersingleftm-ida-local.p12
mosip.mock.sbi.file.iris.single.keys.keyalias.ftm=mosipfingersingleftm
mosip.mock.sbi.file.iris.single.keys.keystorepwd.ftm=mosipfingersingleftm
```

Auth server and IDA certificate endpoints (served by the local WireMock on port `8082`):

```properties
mosip.auth.server.url=http://localhost:8082/v1/authmanager/authenticate/clientidsecretkey
mosip.auth.appid=a
mosip.auth.clientid=a
mosip.auth.secretkey=a

mosip.ida.server.url=http://localhost:8082/idauthentication/v1/internal/getCertificate?applicationId=IDA&referenceId=IDA-FIR
```

### 7.3 Run

Run the MockMDS from your IDE (VS Code `launch.json` or equivalent) or from a `.sh` / `.bat` script, passing the properties above as Java arguments. Example `run-mock-mds.sh` (replace `<MockMDS-main-class-or-jar>` with the value from your `launch.json` `mainClass` / built jar):

```bash
#!/bin/bash
java \
  "-Dmosip.mock.sbi.file.face.keys.keystorefilename.ftm=/Biometric Devices/Finger/Single/Keys/mosipfingersingleftm-ida-local.p12" \
  -Dmosip.mock.sbi.file.face.keys.keyalias.ftm=mosipfingersingleftm \
  -Dmosip.mock.sbi.file.face.keys.keystorepwd.ftm=mosipfingersingleftm \
  "-Dmosip.mock.sbi.file.iris.single.keys.keystorefilename.ftm=/Biometric Devices/Finger/Single/Keys/mosipfingersingleftm-ida-local.p12" \
  -Dmosip.mock.sbi.file.iris.single.keys.keyalias.ftm=mosipfingersingleftm \
  -Dmosip.mock.sbi.file.iris.single.keys.keystorepwd.ftm=mosipfingersingleftm \
  -Dmosip.auth.server.url=http://localhost:8082/v1/authmanager/authenticate/clientidsecretkey \
  -Dmosip.auth.appid=a -Dmosip.auth.clientid=a -Dmosip.auth.secretkey=a \
  "-Dmosip.ida.server.url=http://localhost:8082/idauthentication/v1/internal/getCertificate?applicationId=IDA&referenceId=IDA-FIR" \
  <MockMDS-main-class-or-jar>
```

On Windows, put the same `-D` arguments in a `.bat` file (use `^` for line continuation and quote values containing spaces or `&`). Once running, MockMDS listens on `http://localhost:4501`.

---

## Local Services Reference

| Service | URL |
|---------|-----|
| Config Server | `http://localhost:51000/config` |
| WireMock (mock APIs) | `http://localhost:8082` |
| BioSDK Service | `http://localhost:8083` |
| PostgreSQL | `localhost:5455` |
| Authentication Service (auth, OTP, KYC, internal APIs) | `http://localhost:8090/idauthentication/v1` |
| Mock MDS | `http://localhost:4501` |

---

## Files in `local-dev-setup/`

| File / Directory | Purpose |
|------------------|---------|
| `prepare-biosdk-mock-lib.sh` | Downloads the mock BioSDK JAR and packages it as `mock-sdk.zip` (macOS/Linux) |
| `prepare-biosdk-mock-lib.bat` | Same as above for Windows |
| `docker-compose/docker-compose.yml` | Starts all required backing services |
| `docker-compose/init.sql` | Initialises the IDA database schema and seed data (uses the Spring Batch 6 sequence `BATCH_JOB_INSTANCE_SEQ`) |
| `docker-compose/wiremock/mappings/master-data.json` | WireMock stubs for master-data, notification, OTP manager, and WebSub APIs |
| `docker-compose/wiremock/__files/mock-sdk.zip` | Mock BioSDK bundle served to the BioSDK service on startup (**generated** — run `prepare-biosdk-mock-lib.sh/.bat` first) |
| `mosip-ida-ks.p12` | PKCS12 keystore used by the key manager (replaces SoftHSM) |
| `partner-chain.p12` | Partner certificate chain for local testing |
| `partner-mosip-signed.p12` | MOSIP-signed partner certificate for local testing |

> See `local-dev-setup/README.md` for additional notes on the optional `DevEnvIDABioAPIFactory` (makes the BioSDK provider fault-tolerant at startup) and the `MasterDataCacheInitializer` replacement (loads master-data from a static JSON file instead of calling the live service).

---

## Troubleshooting

**Config server returns 404 for properties**  
Verify the volume path in `docker-compose.yml` points to the correct `mosip-config` directory and that you have edited the right property files.

**503 errors when calling downstream services**  
This is caused by Spring Cloud LoadBalancer treating `localhost` as a service-registry name. The property `spring.cloud.loadbalancer.enabled=false` is already included in the `bootstrap.properties` steps above for the authentication service. If you still see this error, verify the property is present in the JAR's `bootstrap.properties` (rebuild with `mvn clean install -Dgpg.skip=true` if needed).

**Keystore file not found at startup**  
Double-check the absolute path set in `mosip.kernel.keymanager.hsm.config-path`. Use forward slashes even on Windows.

**BioSDK service fails to start**  
The `biosdk-service` depends on `mock-service` being healthy. Check that the WireMock container started successfully (`docker compose logs mock-service`) before the BioSDK container attempts to download the SDK zip.
