# ID Authentication API Test Rig

## Overview

The **ID Authentication API Test Rig** is designed for the execution of module-wise automation API tests for the ID authentication services. This test rig utilizes **Java REST Assured** and **TestNG** frameworks to automate testing of the ID authentication API functionalities. The key focus is to validate the Bio auth, OTP auth, kyc, Auth lock, Auth unlock and related functionalities provided by the ID authentication module.

---

## Test Categories

- **Smoke**: Contains only positive test scenarios for quick verification.
- **Regression**: Includes all test scenarios, covering both positive and negative cases.

---

## Coverage

This test rig covers only **external API endpoints** exposed by the ID authentication module.

---

## Pre-requisites

Before running the automation tests, ensure the following software is installed on the machine:

- **Java 21** ([download here](https://jdk.java.net/))
- **Maven 3.9.6** or higher ([installation guide](https://maven.apache.org/install.html))
- **Lombok** (Refer to [Lombok Project](https://projectlombok.org/))

No Maven `settings.xml` is needed: `pom.xml` declares the repositories it
uses (Maven Central and the Central snapshots repository, which publishes
`apitest-commons`).

### For Windows

- **Git Bash 2.18.0** or higher
- Clone into a short path (for example `C:\work`), or enable long paths once
  with `git config --system core.longpaths true`; some file paths in the
  repository exceed the Windows 260-character limit when cloned deep.

---

## Access Test Automation Code

You can access the test automation code using either of the following methods:

### From Browser

1. Clone or download the repository as a zip file from [GitHub](https://github.com/mosip/id-authentication).
2. Unzip the contents to your local machine.
3. Open a terminal (Linux) or command prompt (Windows) and continue with the following steps.

### From Git Bash

1. Copy the Git repository URL: `https://github.com/mosip/id-authentication`
2. Open **Git Bash** on your local machine.
3. Run the following command to clone the repository:
   ```sh
   git clone https://github.com/mosip/id-authentication
   ```
---

## Update the property file
1. Navigate to the Ida.properties file located at:
    id-authentication\api-test\src\main\resources\config\Ida.properties
2. Open the file in your preferred editor
3. Update the client secret values and other required credentials as per your environment

This is for environment runs only. A local run (see [Running Against a Local
IDA](#running-against-a-local-ida-local-run-mode)) needs no change here:
`Ida-local.properties` overrides every value the local run uses.

---

## Build Test Automation Code

Once the repository is cloned or downloaded, follow these steps to build and install the test automation code:

1. Navigate to the project directory:
   ```sh
   cd api-test
   ```

2. Build the project using Maven:
   ```sh
   mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true
   ```

This will download the required dependencies and prepare the test suite for execution.

---

## Execute Test Automation Suite

You can execute the test automation code using either of the following methods:

### Using Jar

To execute the tests using Jar, use the following steps:

1. Navigate to the `target` directory where the JAR file is generated:
   ```sh
   cd target/
   ```

2. Run the automation test suite JAR file:
   ```sh
   # Linux / macOS / Git Bash
   java -Dmodules=auth -Denv.user=api-internal.<env_name> -Denv.endpoint=<base_env> -Denv.testLevel=smokeAndRegression -jar apitest-auth-*-jar-with-dependencies.jar
   ```
   ```powershell
   # Windows PowerShell (does not expand * for java, so resolve the file first)
   java -Dmodules=auth -Denv.user=api-internal.<env_name> -Denv.endpoint=<base_env> -Denv.testLevel=smokeAndRegression -jar (Get-ChildItem apitest-auth-*-jar-with-dependencies.jar).Name
   ```
   
# Using Eclipse IDE

To execute the tests using Eclipse IDE, use the following steps:

## 1. **Install Eclipse (Latest Version)**
   - Download and install the latest version of Eclipse IDE from the [Eclipse Downloads](https://www.eclipse.org/downloads/).

## 2. **Import the Maven Project**

   After Eclipse is installed, follow these steps to import the Maven project:

   - Open Eclipse IDE.
   - Go to `File` > `Import`.
   - In the **Import** wizard, select `Maven` > `Existing Maven Projects`, then click **Next**.
   - Browse to the location where the `api-test` folder is saved (either from the cloned Git repository or downloaded zip).
   - Select the folder, and Eclipse will automatically detect the Maven project. Click **Finish** to import the project.

## 3. **Build the Project**

   - Right-click on the project in the **Project Explorer** and select `Maven` > `Update Project`.
   - This will download the required dependencies as defined in the `pom.xml` and ensure everything is correctly set up.

## 4. **Run the Tests**

   To execute the test automation suite, you need to configure the run parameters in Eclipse:

   - Go to `Run` > `Run Configurations`.
   - In the **Run Configurations** window, create a new configuration for your tests:
     - Right-click on **Java Application** and select **New**.
     - In the **Main** tab, select the project by browsing the location where the `api-test` folder is saved, and select the **Main class** as `io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner`.
   - In the **Arguments** tab, add the necessary **VM arguments**:
     - **VM Arguments**:
    
    ```
    -Dmodules=auth -Denv.user=api-internal.<env_name> -Denv.endpoint=<base_env> -Denv.testLevel=smokeAndRegression
    ```

## 5. **Run the Configuration**

   - Once the configuration is set up, click **Run** to execute the test suite.
   - The tests will run, and the results will be shown in the **Console** tab of Eclipse.

   **Note**: You can also run in **Debug Mode** to troubleshoot issues by setting breakpoints in your code and choosing `Debug` instead of `Run`.

## 6. **View Test Results**

   - After the tests are executed, you can view the detailed results in the `api-test\testng-report` directory.
   - The report will have two sections:
       - One section for pre-requisite APIs test cases.
       - Another section for core test cases.

---

## Running Against a Local IDA (Local Run Mode)

Local run mode points the rig at a locally running IDA and skips every other
MOSIP service. Only `/idauthentication/**` endpoints are exercised; the data the
prerequisite suite would normally create through idrepo, resident,
partner-management and policy-manager is read from seeded files instead.

Local run mode is **off by default**. With the flag unset the rig behaves
exactly as it does against a deployed environment.

### First-time setup, step by step

Paths below are relative to the id-authentication repo root unless stated.

You need Java 21, Maven 3.9.6+, Docker with Docker Compose (Docker Desktop
running on Windows/macOS; the `docker` command must work in the shell that
runs the rig, see [OTP in local runs](#otp-in-local-runs)) and Git.

1. **apitest-commons** (nothing to do normally). The rig depends on
   `io.mosip.testrig.apitest.commons:apitest-commons:1.8.0-SNAPSHOT`, which the
   build downloads from the Central snapshots repository. Only to test
   unreleased commons changes, clone `https://github.com/mosip/mosip-functional-tests`
   (branch `develop`) and install it into your local Maven repository first:
   ```
   cd mosip-functional-tests/apitest-commons
   mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true
   ```
2. **Prepare the local IDA stack** as described in
   `authentication/local-dev-setup/README.md`:
   - a local checkout of `mosip-config`, mounted into the config-server
     container through `MOSIP_CONFIG_PATH` in `docker-compose/.env` (copy
     `.env.example` to `.env`), with the Spring Boot 4 settings that README
     lists (`preferred-json-mapper=jackson2`);
   - the `kernel-auth-adapter` dependency added to
     `authentication-service/pom.xml` (section 5.1 there: the adapter is not
     bundled in the jar);
   - `bootstrap.properties` of `authentication-service` pointing at the local
     config server (`http://localhost:51000/config`) and the local hosts, with
     `spring.cloud.loadbalancer.enabled=false` (section 5.2 there).
3. **Local IDA settings on top of `local-dev-setup/README.md`.** That guide
   already covers the allowed domain `localhost`, the first / middle / last name
   mapping, `preferred-json-mapper=jackson2`, removing `ANT_PATH_MATCHER` and the
   PKCS12 keystore; follow it for those. In addition:

   *Needed by any local IDA, not yet in `local-dev-setup/README.md`* (skip a
   row once that guide covers it):

   | Where | Setting | Why |
   |---|---|---|
   | `mosip-config/id-authentication-default.properties` | `mosip.ida.oidc4ida.ignore.standard.claims.list=picture,individual_id,gender,residenceStatus` | IDA does not start without it (no default; kyc-auth / kyc-exchange v2, #1818) |
   | `mosip-config/id-authentication-default.properties` | `mosip.kernel.keymanager.jwtEncrypt.validate.json=false` | kyc-exchange fails without it: IDA passes a signed JWS, not JSON, to `jwtEncrypt` |
   | `authentication-service/pom.xml` | `bcprov-jdk18on` declared before the `kernel-auth-adapter` dependency from step 2 (see 3b) | otherwise the unsigned BouncyCastle copy inside the adapter jar loads first and every eKYC response returns `kycStatus:false` |

   *Needed only for the rig*:

   | Where | Setting | Why |
   |---|---|---|
   | `mosip-config/id-authentication-default.properties` | `otp.request.flooding.max-count=300` (default config: 100) | the rig sends many OTPs to one seeded UIN within a minute, so 100 per minute is exceeded (`IDA-OTA-001`); same value as dev1 |
   | `mosip-config/id-authentication-default.properties` | `mosip.ida.idp.issuer.uri=mosip-ida` | the kyc-exchange tests check `iss`, which IDA adds only when this is set (default: empty) |
   | `mosip-config/id-authentication-default.properties` | `mosip.ida.idp.no.language.attribute.list=phone,birthdate,email` | the v2 claim tests expect these claims without a language suffix (default: `phone`) |
   | `local-dev-setup/docker-compose/wiremock/mappings/master-data.json` | both token stubs return a JWT-shaped token carrying the MOSIP roles (see 3a) | IDA reads the roles from the token on flows the rig drives; `DUMMY.AUTH.TOKEN` has none |

   The OTP stub in `master-data.json` stays as it is (random OTP): the rig
   reads each generated OTP from the mock container's log, see
   [OTP in local runs](#otp-in-local-runs).

   **3a. Token with roles.** In `master-data.json`, replace both
   `"access_token": "DUMMY.AUTH.TOKEN"` values with the token below. It is a
   local test token, not a secret: its signature is the text
   `local-dev-signature-not-verified`, and its payload carries the MOSIP roles
   (`ID_AUTHENTICATION`, `RESIDENT`, `PARTNER_ADMIN`, ...). The local IDA reads
   the roles and does not verify the signature.
   ```
   eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImxvY2FsIn0.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODIvYXV0aC9yZWFsbXMvbW9zaXAiLCJzdWIiOiJsb2NhbC10ZXN0cmlnIiwiYXpwIjoibW9zaXAtdGVzdHJpZy1jbGllbnQiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsb2NhbC10ZXN0cmlnIiwiZXhwIjoyMjM0NTY3ODkwLCJpYXQiOjE3MDAwMDAwMDAsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJQQVJUTkVSX0FETUlOIiwiR0xPQkFMX0FETUlOIiwiQVVUSF9QQVJUTkVSIiwiUE1TX0FETUlOIiwiUE9MSUNZTUFOQUdFUiIsIklEX0FVVEhFTlRJQ0FUSU9OIiwiUkVHSVNUUkFUSU9OX0FETUlOIiwiUkVHSVNUUkFUSU9OX1NVUEVSVklTT1IiLCJSRUdJU1RSQVRJT05fT0ZGSUNFUiIsIlJFR0lTVFJBVElPTl9QUk9DRVNTT1IiLCJQUkVfUkVHSVNUUkFUSU9OX0FETUlOIiwiWk9OQUxfQURNSU4iLCJJTkRJVklEVUFMIiwiUkVTSURFTlQiLCJJRF9SRVBPU0lUT1JZIiwiQ1JFREVOVElBTF9SRVFVRVNUIiwiSE9UTElTVF9BRE1JTiIsImRlZmF1bHQtcm9sZXMtbW9zaXAiXX19.bG9jYWwtZGV2LXNpZ25hdHVyZS1ub3QtdmVyaWZpZWQ
   ```

   **3b. BouncyCastle order.** In `authentication-service/pom.xml`, declare
   `bcprov-jdk18on` directly before the `kernel-auth-adapter` dependency you
   added in step 2:
   ```xml
   <dependency>
       <groupId>org.bouncycastle</groupId>
       <artifactId>bcprov-jdk18on</artifactId>
       <version>1.85</version>
   </dependency>
   <dependency>
       <groupId>io.mosip.kernel</groupId>
       <artifactId>kernel-auth-adapter</artifactId>
       <version>${kernel-auth-adapter.version}</version>
   </dependency>
   ```
   Use the version `bcpkix-jdk18on` resolves to (1.85 with kernel 1.4.1; check
   with `mvn dependency:tree -Dincludes=org.bouncycastle`).

   These are local-only edits; do not commit them to the env config branches.
4. **Start the stack on a fresh database** (from
   `authentication/local-dev-setup/docker-compose`):
   ```
   docker compose down -v
   docker compose up -d
   ```
5. **Start `authentication-service`** on port 8090, in UTC:
   ```
   cd authentication/authentication-service
   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"
   ```
   Wait until `http://localhost:8090/idauthentication/v1/actuator/health`
   reports `UP`.
6. **Switch the rig to the local files and build** (from `api-test/`, see
   [Switch the rig to the local files](#switch-the-rig-to-the-local-files)):
   ```
   cp application-local.properties application.properties
   mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true
   ```
7. **Run smoke** with `mvn exec:java` (see [Run](#run)), then, for regression,
   repeat steps 4–5 (fresh database, restart the service) and run
   `smokeAndRegression`.
8. **Clean up**: stop the service, `docker compose down -v`, and restore the
   env file with `git checkout -- application.properties`.

### Prerequisites

1. The local IDA stack from `authentication/local-dev-setup` is up, with a
   **freshly created database for every run** (see [Fresh database per
   run](#fresh-database-per-run)).
2. `authentication-service` is running on port **8090**. The OTP service is
   merged into it, so this one process serves `/auth`, `/otp`, `/kyc` and
   `/kyc-exchange`.
3. The run command's endpoint carries this port
   (`-Denv.endpoint=http://localhost:8090`); change it there if your service
   runs on another port.

### Switch the rig to the local files

`application.properties` has a local variant that must be copied over the
environment one before a local run (run from `api-test/`):

```
cp application-local.properties application.properties
```

| File | Environment version | Local version |
|---|---|---|
| `application.properties` | Mock SBI config for a deployed env | Mock SBI config for local IDA (Auth devices, localhost URLs, local keys) |

To go back to an environment run, restore the original:

```
git checkout -- application.properties
```

After switching in either direction, rebuild (or run `mvn process-resources`)
so `target/classes` picks up the copied file.

Do not commit the copied file.

### Run

From `api-test/`, after the build in step 6:

```sh
# Linux / macOS / Git Bash
mvn exec:java -Dexec.mainClass=io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner \
  -DlocalRun=yes -Denv.user=local -Denv.endpoint=http://localhost:8090 \
  -DtransactionId=1234567890 -Denv.testLevel=smoke
```
```powershell
# Windows PowerShell (quote each -D argument, PowerShell splits them at the dot otherwise)
mvn exec:java "-Dexec.mainClass=io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner" "-DlocalRun=yes" "-Denv.user=local" "-Denv.endpoint=http://localhost:8090" "-DtransactionId=1234567890" "-Denv.testLevel=smoke"
```

Use `-Denv.testLevel=smokeAndRegression` for the full run. The first run
downloads the `exec-maven-plugin`. Wait for one run to exit before starting
the next: each run clears the staged test resources at start-up. The report is
written to `api-test/testng-report/`.

> **Run local mode from the classes, not from the jar.** `exec:java` (or an
> IDE, see below) runs the rig from `target/classes` with the dependency jars
> as Maven resolves them. Do not use `java -jar
> apitest-auth-*-jar-with-dependencies.jar` for local runs: the jar carries a
> repacked, unsigned BouncyCastle, which Oracle JDK refuses as a JCE provider
> (`JCE cannot authenticate the provider BC`), so every biometric capture
> fails; and on Linux / macOS the jar looks for the suites in
> `../mosip/testNgXmlFiles` (the Docker image layout) instead of
> `./testNgXmlFiles`.

### Fresh database per run

Start every local run, including a regression run straight after a smoke run,
from a database newly created from `init.sql` (run from
`authentication/local-dev-setup/docker-compose`):

```
docker compose down -v
docker compose up -d
```

Then restart `authentication-service` so it reconnects to the new database.

A run changes data that `init.sql` only sets up once, so a second run on the
same database fails:

- The one-time temporary VID (`8948612871402917`) is deleted by IDA once used.
- The hotlist tests leave the MISP licence key blocked, which fails every
  authentication of the next run.

> **Endpoint and `domainUri`.** The endpoint is the local IDA URL including its
> port (`http://localhost:8090`). The `domainUri` sent in auth requests is set
> separately from `local.domainUri` in `Ida-local.properties` (`localhost`),
> because the local IDA accepts only the bare host as a domain. The rig also
> names its certificate directory after the endpoint host; `IdAuthConfigManager`
> makes that name filesystem-safe in local mode (`localhost:8090` →
> `localhost_8090`), since a colon is invalid in a Windows directory name.
>
> **Why UTC.** The rig sends UTC timestamps (`requestTime`, biometric capture
> times). IDA compares them with its own clock in the JVM's default time zone,
> so on a machine in another zone (IST is 5.5 h ahead) requests fall outside the
> allowed time window. A deployed IDA runs in UTC containers, so start the local
> one with `-Duser.timezone=UTC`.
>
> **Why `-DtransactionId`.** An Auth biometric capture binds its encryption to
> the transactionId, and some yml files hardcode `1234567890`, so the value is
> pinned for the whole run.

From an IDE, use the same values as VM arguments with main class
`io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner` and the `api-test`
folder as the working directory.

`localRun` can also be set as an environment variable or in `Ida.properties`.

### Troubleshooting

| Symptom | Fix |
|---|---|
| `docker compose up` fails, or the database / config server never come up | start Docker (Docker Desktop on Windows/macOS) and wait until `docker info` answers, then repeat step 4 |
| `biosdk_service` stays `Created` and `http://localhost:8083/biosdk-service/` does not answer | it waits for `mock-service` to be healthy; that healthcheck runs every 30 s, so this can take about a minute. If it still stays `Created`, start it with `docker start biosdk_service` |
| Windows: config-server fails with `bind ... 51000 ... forbidden` | Windows has reserved the port (`netsh interface ipv4 show excludedportrange protocol=tcp`). From an admin prompt run `net stop winnat` and `net start winnat`, then repeat step 4 |
| Windows: `git clone` reports `Filename too long` | clone into a short path, or run `git config --system core.longpaths true` and clone again |
| `Could not find or load main class ...MosipTestRunner` | run with `mvn exec:java` from `api-test/` as in [Run](#run), not with plain `java` |
| every biometric test fails with `JCE cannot authenticate the provider BC` | the rig was started from the `jar-with-dependencies`; run it with `mvn exec:java` instead |
| eKYC tests return `kycStatus:false` although the auth passed | the IDA side has the same BouncyCastle problem: see 3b |
| OTP tests fail with `IDA-OTA-001` | set `otp.request.flooding.max-count=300` (step 3) and restart the service |
| OTP auth tests fail with an OTP mismatch, and the log says `no generated OTP found in the mock OTP manager log` or `could not read the mock OTP manager log` | `docker` must be on the PATH of the shell that runs the rig, and the WireMock container must be named `master_data_mock_service` (set `local.otpMockContainer` in `Ida-local.properties` if yours differs); see [OTP in local runs](#otp-in-local-runs) |
| many OTP-based positive tests (OtpAuth, EkycOtp, MultiFactorAuth, OtpAuthDelegated, OtpAuthKycExchange) fail with `IDA-OTA-004` *OTP is invalid*, although the log says `OTP taken from the mock OTP manager log` and an earlier run on the same code passed | the machine is short of memory or CPU, so Docker writes the new OTP to the mock container log late and the rig picks up the previous test's OTP. This depends on the machine, not on the code. Close other heavy applications (check free RAM and Docker Desktop's memory limit), bring the stack down with `docker compose down -v` and run again |

### What local mode changes

| | Environment run | Local run |
|---|---|---|
| Config | `Ida.properties` | `Ida.properties` + `Ida-local.properties` overlay |
| Suite | `authMasterTestSuite.xml`, blocks with `runMode=local` dropped | `authMasterTestSuite.xml`; `runMode=env` blocks reported as ignored, or dropped when a `localReplacedBy` block stands in for them |
| Partner registration | PMS + policy-manager (~11 calls) | seeded values from the local profile |
| IDA certificate | `/idauthentication/v1/internal/getCertificate` | `config/local/ida-partner-local.cer` |
| Request signing key | generated, uploaded to PMS for signing | `config/local/partner-mosip-signed.p12` |
| FTM signing key (`digitalId`) | generated, registered via PMS | `config/local/ftm-signing-local.p12` (temporary, see below) |
| UIN / VID | created via idrepo + resident | `config/local/seed-ids.properties` |
| OTP | mock-SMTP WebSocket listener | read from the mock OTP manager's container log (see below) |
| Keycloak users | created and deleted via admin API | skipped |
| DB cleanup | mosip_keymgr, mosip_ida, mosip_master | skipped |

> **FTM signing key (temporary).** IDA checks every biometric `digitalId`
> against the FTM roots in `ida.ca_cert_store`, and the rig never writes to the
> IDA DB, so the key must chain to the FTM root `init.sql` seeds
> (`CN=RootCAFTM, OU=Tech Center`). None of the FTM keystores under
> `Biometric Devices` do: `mosipfaceftm.p12` chains to a root that expired in
> 2022, and the others chain to a `RootCAFTM, OU=mosip.io` that `init.sql` does
> not carry. `ftm-signing-local.p12` is a key issued by the seeded root, used
> until MockMDS publishes FTM keys that chain to it.

### OTP in local runs

Locally no email or SMS is delivered. IDA asks the mock OTP manager (the
WireMock container `master_data_mock_service`) for an OTP, and WireMock, started
with `--verbose`, prints its rendered response, including the generated OTP,
to the container log. This is the line the
`authentication/local-dev-setup/README.md` sample client tells you to grep:

```
docker compose logs mock-service | grep GENERATION_SUCCESSFUL
```

The rig reads that line itself: after each send-OTP it runs `docker logs --since
120s master_data_mock_service` and uses the newest generated OTP
(`local.otpFromMockLog = true` in `Ida-local.properties`; the container name is
`local.otpMockContainer`). So the WireMock OTP stub keeps its random OTP, and
the machine running the rig needs the `docker` command on its PATH. If no OTP
is found, the rig falls back to `preconfiguredOtp = 111111`. Environment runs
never use this: they read the OTP from the real email or SMS as before.

### Seeded data

The values in `config/Ida-local.properties` and `config/local/seed-ids.properties`
mirror `local-dev-setup/docker-compose/init.sql`. If that file changes, these
must be updated to match — in particular the partner id, API key, MISP licence
key and the test UIN.

### How the local test set differs from an env run

The idrepo, resident and hotlist calls the suite needs (deactivate / activate
UIN, revoke VID, update identity, block / unblock) are served by the WireMock
stubs of the local stack, which also post the matching events to IDA.

The delegated (kyc-auth) and KYC-exchange flows run locally against the seeded
OIDC client `mpartner-default-demo-oidc`.

**One suite for both modes.** Both modes run `authMasterTestSuite.xml`
(`authPrerequisiteSuite.xml` + `authSuite.xml`). A `<test>` block can carry
`<parameter name="runMode" value="local" />` or `value="env"`, and a block
without `runMode` runs in both. `RunModeSuiteFilter` adjusts the blocks before
TestNG starts:

- An env run drops the `runMode=local` blocks.
- A local run keeps the `runMode=env` blocks, but reports every one of their
  testcases as *Ignored* without calling anything, so the local total matches
  the env total. An env block that carries
  `<parameter name="localReplacedBy" value="<local block>" />` is dropped
  instead, because the named local block runs the same testcases on the local
  stack.

A new block added without `runMode` therefore runs in both modes; mark it `env`
if the local stack cannot serve it, and add `localReplacedBy` only when a local
block stands in for it test for test.

| Marked | Blocks |
|---|---|
| `runMode=env`, reported as ignored locally | AddIdentity, OIDCClient, CreateVID, GenerateVidForSendOtpAndDeactivateUIN, the draft blocks, GenerateVID, AuthInternalLock, RegenerateVid, the Retrieve* blocks, GetUnusedUIN (prerequisite suite), and AuthTransaction and UpdateIdentity (`authSuite.xml`) |
| `runMode=env` with `localReplacedBy` | UpdateIdentityForUin, UpdateIdentityToActivate, RevokeVID, AuthLock (prerequisite suite) and AuthUnLock (`authSuite.xml`); each is replaced by the `*Local` / `*NewLocal` block of the same name |
| `runMode=local` | DemoAuthLocalTempVid, UpdateIdentityForUinLocal, UpdateIdentityToActivateLocal, RevokeVIDLocal, AuthLockNewLocal (prerequisite suite), AuthUnLockNewLocal (`authSuite.xml`), and the four `DeactivatedUin*` blocks, each next to the env block it stands in for |
| none (both) | DeactivateVIDs, ReactivateVIDs and all other `authSuite.xml` blocks |

DemoAuthLocalTempVid stands in for TC_IDA_GenerateVID_05 (the env run creates
the one-time temporary VID there; the local run uses the seeded one), so the
GenerateVID block leaves that testcase out locally
(`localExcludeTestCases`). The four `DeactivatedUin*` tests move out of their
main blocks locally (see below). With these, a local run has the same total as
an env run (1037 for `smokeAndRegression`).

AuthUnLockNewLocal sits after EkycDemo rather than next to AuthUnLock:
locally the lock tests share one seeded UIN, and EkycDemo_48 / _49 need its
DOB still locked.

Why the env-only blocks are reported as ignored locally:

| Block | Reason |
|---|---|
| AuthTransaction, `*Internal*` | not tried locally yet; the internal endpoints are now served by `authentication-service` itself |
| UpdateIdentity (demographic update) | needs idrepo to store and publish the updated identity |
| AuthLock / AuthUnLock (deprecated resident API) | replaced by AuthLockNewLocal / AuthUnLockNewLocal, which call the new resident API stub |
| the other env prerequisite blocks | create partners, identities and VIDs that the local run takes from `init.sql` |

**Local-only test files**, used only by `runMode=local` blocks:

| File | Purpose |
|---|---|
| `ida/DemoAuth/DemoAuthLocalOnly.yml` | consumes the one-time temporary VID so TC_IDA_DemoAuth_28 / _49 find it spent (env runs reach that state by the VID expiring) |
| `ida/LocalMockRequests/*.yml` | deactivate / activate UIN, revoke VID and update identity sent straight to the local stubs (no send-OTP step) |
| `ida/AuthLockUnlockNew/AuthLockNewLocal.yml`, `AuthUnLockNewLocal.yml` | replicas of the deprecated AuthLock / AuthUnLock tests on the new resident API |

**Tests that run with local data.** `local.override.<testcase>.<key>` entries
in `Ida-local.properties` point a test at a seeded UIN / VID instead of the one
the env prerequisite suite creates, and the `<id>.dob` / `.name` / `.gender`
entries swap the demographic values the yml files hardcode for the seeded
identity's own. The deactivated-UIN tests (TC_IDA_DemoAuth_61,
TC_IDA_EkycBio_49, TC_IDA_SendOTP_04, TC_IDA_BioAuthDelegatedV2Neg_31) run in
the `DeactivatedUin*` blocks of the prerequisite suite, while their UIN is
deactivated (`localIncludeTestCases`), and are left out of their main
`authSuite.xml` blocks locally (`localExcludeTestCases`). Both parameters are
ignored on env runs.

**Ignored only in local runs.** These run on env. Locally they are skipped
and counted under *Ignored* in the report:

| Testcases | Listed in | Reason |
|---|---|---|
| MultiFactorAuth NOAUTH (no resident token) | `local.skipTestCases` | expects a gateway to reject the request; there is no gateway locally |
| BlockHotlistStatus_01 | `local.skipTestCases` | `GET /v1/hotlist/status/UIN/{id}` has no stub |
| TC_IDA_DemoAuth_76–79 | `local.noSeedDataTestCases` | need partners mapped to one policy with `authTokenType=policy` |
| TC_IDA_DemoAuth_82 | `local.noSeedDataTestCases` | needs a second partner on an `authTokenType=partner` policy |
| TC_IDA_DemoAuth_83–86 | `local.noSeedDataTestCases` | need partners mapped to one policy with `authTokenType=random` |
| TC_IDA_BioAuthDelegated_26, TC_IDA_BioAuthKycExchange_28 | `local.noSeedDataTestCases` | need a MISP key whose policy does not allow kyc delegation |
| TC_IDA_MultiFactorAuth_71 | `local.noSeedDataTestCases` | needs a partner policy with IRIS enabled and bio auth enforced |
| TC_IDA_EkycDemo_51 | `local.noSeedDataTestCases` | needs a partner policy without the requested kyc attribute |
| 30 v2 verified-claims tests: TC_IDA_BioAuthDelegatedV2_07, 09, 12, 13, 18, 25, 30–35, 37, 39, 40, 42 and TC_IDA_BioAuthKycExchangeV2_02, 04, 06, 07, 09, 12, 14–21 | `local.noSeedDataTestCases` | need identities with `verifiedAttributes` (the env AddIdentity `ForVerifiedNameDob`, `ForVerifiedClaims`, `ForMaxAgeFiltered`) |
| TC_IDA_BioAuthDelegatedV2Neg_24, TC_IDA_BioAuthKycExchangeV2Neg_12 | `local.noSeedDataTestCases` | invalid IdP licence key: need a delegated partner whose certificate matches the rig signing key, so IDA reaches the licence check (`IDA-MPA-007`) |

On env, PMS and idrepo create the data the `local.noSeedDataTestCases` tests
need. The local `init.sql` does not seed it yet; once it does, remove the
testcase from the list.

**Expected output that differs locally.** `local.override.output.<testcase> =
<from>=><to>` entries in `Ida-local.properties` rewrite a testcase's expected
output in local runs only; env runs keep the yml value.

| Testcases | Override | Cause |
|---|---|---|
| TC_IDA_EkycBio_49, TC_IDA_BioAuthDelegatedV2Neg_31 | `IDA-MLC-018=>IDA-MLC-022` | for the mock-deactivated UIN, IDA returns `IDA-MLC-022`; env returns `IDA-MLC-018` (under check). Remove the entries once both return the same code. |
| TC_IDA_SendOTP_04 | `IDA-MLC-018=>IDA-MLC-023` | for a VID of the mock-deactivated UIN, IDA returns `IDA-MLC-023`; env returns `IDA-MLC-018` (under check). |

The kyc-exchange v2 `_Decode_` tests need the `init.sql` from
id-authentication#1822 or later, where `mpartner-default-demo-oidc` carries
the same certificate as `config/local/partner-mosip-signed.p12`.

**Known issues** come from the same `testCaseSkippedList.txt` in both modes
and are counted under *Known Issues*. A testcase that is a known issue only
locally goes into `local.skipTestCases` in `Ida-local.properties` instead.
For example, TC_IDA_HotlistAPI_05 (malformed `expiryTimestamp`) is MOSIP-41020,
as in the IDA 1.3.x release branches: the hotlist service returns `ADM-HTL-004`
instead of `ADM-HTL-002`.

---

## Test Report Column Definitions
This section describes the meaning of each column in the test report:
- **Total (T)**
  The total number of test cases considered in the report.
- **Passed (P)**
  Indicates the number of test cases that executed successfully with the expected results.
- **Failed (F)**
  Indicates the number of test cases that failed due to issues such as output validation mismatches or unexpected errors during execution.
- **Skipped (S)**
  Represents test cases that were not executed due to missing prerequisites or data dependencies.
- **Ignored (I)**
  Represents test cases that were intentionally not executed due to limitations such as unsupported features, incompatibilities, or undeployed services.
- **Known Issues (KI)**
  Indicates test cases that failed but are already acknowledged as known issues for the current release, typically linked with a bug or defect ID.
  
---

## Details of Arguments Used

- **env.user**: Replace `<env_name>` with the appropriate environment name (e.g., `dev`, `qa`, etc.).
- **env.endpoint**: The environment where the application under test is deployed. Replace `<base_env>` with the correct base URL for the environment (e.g., `https://api-internal.<env_name>.mosip.net`).
- **env.testLevel**: Set this to `smoke` to run only smoke test cases, or `smokeAndRegression` to run both smoke and regression tests.
- **jar**: The JAR built in `target/`, named `apitest-auth-<version>-jar-with-dependencies.jar`, where `<version>` is the `<version>` in `pom.xml`. The `apitest-auth-*-jar-with-dependencies.jar` pattern above picks it up, so the command does not change when the version does.

### Build and Run Info

To run the tests for both **Smoke** and **Regression**:

1. Ensure the correct environment and test level parameters are set.
2. Execute the tests as shown in the command above to validate ID authentication API functionalities.

---

## License

This project is licensed under the terms of the [Mozilla Public License 2.0](https://github.com/mosip/mosip-platform/blob/master/LICENSE)