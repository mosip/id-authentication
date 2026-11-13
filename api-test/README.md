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
- **setting.xml** ([download here](https://github.com/mosip/mosip-functional-tests/blob/master/settings.xml))

### For Windows

- **Git Bash 2.18.0** or higher
- Ensure the `settings.xml` file is present in the `.m2` folder.

### For Linux

- The `settings.xml` file should be present in two places:
  - In the regular Maven configuration folder (`/conf`)
  - Under `/usr/local/maven/conf/`

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

1. **Build apitest-commons.** The rig depends on the `1.8.0-SNAPSHOT` of
   `mosip-functional-tests/apitest-commons`. Build it from `develop` into the
   local Maven repository:
   ```
   cd mosip-functional-tests/apitest-commons
   mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true
   ```
2. **Prepare the local IDA stack** as described in
   `authentication/local-dev-setup/README.md`:
   - a local checkout of `mosip-config`, mounted into the config-server
     container (`docker-compose.yml`, `volumes:`), with the Spring Boot 4
     settings that README lists (`preferred-json-mapper=jackson2`);
   - the `kernel-auth-adapter` dependency added to
     `authentication-service/pom.xml` (section 5.1 there: the adapter is not
     bundled in the jar);
   - `bootstrap.properties` of `authentication-service` pointing at the local
     config server (`http://localhost:51000/config`) and the local hosts, with
     `spring.cloud.loadbalancer.enabled=false` (section 5.2 there).
3. **Local IDA settings the rig needs**, on top of `local-dev-setup/README.md`:

   | Where | Setting | Why |
   |---|---|---|
   | `mosip-config/id-authentication-default.properties` | `mosip.ida.allowed.domain.uris` also lists `localhost` | the rig sends `domainUri=localhost` (see *Why no port in the endpoint*) |
   | `mosip-config/id-authentication-default.properties` and `identity-mapping.json` | name mapped to `firstName,middleName,lastName` instead of `fullName` | the seeded identities in `init.sql` carry first / middle / last names |
   | `mosip-config/id-authentication-default.properties` | `mosip.kernel.keymanager.jwtEncrypt.validate.json=false` | kyc-exchange JWE responses (same as upstream config) |
   | `mosip-config/id-authentication-default.properties` | `mosip.ida.oidc4ida.ignore.standard.claims.list` set | required by kyc-auth / kyc-exchange v2 (#1818), has no default |
   | `mosip-config/id-authentication-default.properties` | `mosip.ida.idp.no.language.attribute.list=phone,birthdate,email` | same as the env config |
   | `mosip-config/id-authentication-default.properties` | `mosip.ida.idp.issuer.uri=mosip-ida` | kyc-exchange responses carry `iss` only when this is set |
   | `mosip-config/id-authentication-default.properties` | `spring.http.converters.preferred-json-mapper=jackson2` | Spring Boot 4 defaults to Jackson 3; IDA uses Jackson 2 |
   | `mosip-config/application-default.properties` | `spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER` commented out | not supported by Spring Boot 4 |
   | `mosip-config/id-authentication-default.properties` | keymanager uses the PKCS12 keystore `local-dev-setup/mosip-ida-ks.p12` | no HSM locally |
   | `local-dev-setup/docker-compose/wiremock/mappings/master-data.json` | OTP stub returns the fixed OTP `111111` | the rig uses `preconfiguredOtp = 111111` (`Ida-local.properties`) |
   | same file | token stubs return a JWT-shaped token carrying the MOSIP roles | IDA reads the roles from the token |
   | `authentication-service/pom.xml` | `bcprov-jdk18on` declared before the `kernel-auth-adapter` dependency from step 2, at the version `bcpkix-jdk18on` resolves to (1.85 with kernel 1.4.1; check with `mvn dependency:tree -Dincludes=org.bouncycastle`) | otherwise the unsigned BouncyCastle copy inside the adapter jar loads first and every eKYC response returns `kycStatus:false` |

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
6. **Start the 8080 → 8090 relay** (see Prerequisites, item 3), for example:
   ```
   node -e "const n=require('net');n.createServer(c=>{const u=n.connect(8090,'127.0.0.1',()=>{c.pipe(u);u.pipe(c)});u.on('error',()=>c.destroy());c.on('error',()=>u.destroy())}).listen(8080,'127.0.0.1')"
   ```
7. **Switch the rig to the local files and build** (from `api-test/`, see
   [Switch the rig to the local files](#switch-the-rig-to-the-local-files)):
   ```
   cp application-local.properties application.properties
   cp src/main/resources/testCaseSkippedList-local.txt src/main/resources/testCaseSkippedList.txt
   mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true
   ```
8. **Run smoke** (see [Run](#run)), then, for regression, repeat steps 4–5
   (fresh database, restart the service) and run `smokeAndRegression`.
9. **Clean up**: stop the service and the relay, `docker compose down -v`,
   and restore the env files with
   `git checkout -- application.properties src/main/resources/testCaseSkippedList.txt`.

### Prerequisites

1. The local IDA stack from `authentication/local-dev-setup` is up, with a
   **freshly created database for every run** (see [Fresh database per
   run](#fresh-database-per-run)).
2. `authentication-service` is running on port **8090**. The OTP service is
   merged into it, so this one process serves `/auth`, `/otp`, `/kyc` and
   `/kyc-exchange`.
3. A TCP relay forwards port **8080** to **8090** (any simple TCP proxy, e.g. a
   few lines of Node `net`). The endpoint below carries no port, so REST-Assured
   sends to 8080.

### Switch the rig to the local files

Two files have a local variant that must be copied over the environment one
before a local run (run from `api-test/`):

```
cp application-local.properties application.properties
cp src/main/resources/testCaseSkippedList-local.txt src/main/resources/testCaseSkippedList.txt
```

| File | Environment version | Local version |
|---|---|---|
| `application.properties` | Mock SBI config for a deployed env | Mock SBI config for local IDA (Auth devices, localhost URLs, local keys) |
| `testCaseSkippedList.txt` | Known issues for env runs | Known issues for local runs |

To go back to an environment run, restore the originals:

```
git checkout -- application.properties src/main/resources/testCaseSkippedList.txt
```

After switching in either direction, rebuild (or run `mvn process-resources`)
so `target/classes` picks up the copied files.

Do not commit the copied files.

### Run

```
java -DlocalRun=yes -Denv.user=local -Denv.endpoint=http://localhost -DtransactionId=1234567890 -Denv.testLevel=smoke io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner
```

Use `-Denv.testLevel=smokeAndRegression` for the full run. Wait for one run to
exit before starting the next: each run clears the staged test resources at
start-up.

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

> **Why no port in the endpoint.** The rig names its certificate directory and
> builds the request `domainUri` from the endpoint host, and the local IDA only
> accepts the bare host `localhost` as the domain. `IdAuthConfigManager` also
> rewrites `BaseTestCase.domain` to a filesystem-safe form in local mode, since a
> colon in the directory name is invalid on Windows.
>
> **Why `-DtransactionId`.** An Auth biometric capture binds its encryption to
> the transactionId, and some yml files hardcode `1234567890`, so the value is
> pinned for the whole run.

From an IDE, use the same values as VM arguments with main class
`io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner`.

`localRun` can also be set as an environment variable or in `Ida.properties`.

### What local mode changes

| | Environment run | Local run |
|---|---|---|
| Config | `Ida.properties` | `Ida.properties` + `Ida-local.properties` overlay |
| Suite | `authMasterTestSuite.xml` | `authLocalSuite.xml` |
| Partner registration | PMS + policy-manager (~11 calls) | seeded values from the local profile |
| IDA certificate | `/idauthentication/v1/internal/getCertificate` | `config/local/ida-partner-local.cer` |
| Request signing key | generated, uploaded to PMS for signing | `config/local/partner-mosip-signed.p12` |
| FTM signing key (`digitalId`) | generated, registered via PMS | `config/local/ftm-signing-local.p12` (temporary, see below) |
| UIN / VID | created via idrepo + resident | `config/local/seed-ids.properties` |
| OTP | mock-SMTP WebSocket listener | fixed OTP via `usePreConfiguredOtp` |
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

**Blocks not in `authLocalSuite.xml`** (the local stack cannot serve them):

| Block | Reason |
|---|---|
| AuthTransaction, `*Internal*` | not enabled in the local suite yet; the internal endpoints are now served by `authentication-service` itself, but these blocks have not been tried locally |
| UpdateIdentity (demographic update) | needs idrepo to store and publish the updated identity |
| AuthLock / AuthUnLock (deprecated resident API) | replaced by AuthLockNewLocal / AuthUnLockNewLocal, which call the new resident API stub |

**Local-only test files**, used by `authLocalSuite.xml` and by no env suite:

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
the `DeactivatedUin*` blocks,
while their UIN is deactivated (`localIncludeTestCases` /
`localExcludeTestCases` in `authLocalSuite.xml`).

**Ignored only in local runs.** These run on env. Locally they are skipped
and counted under *Ignored* in the report:

| Testcases | Listed in | Reason |
|---|---|---|
| MultiFactorAuth NOAUTH (no resident token) | `local.skipTestCases` | expects a gateway to reject the request; there is no gateway locally |
| TC_IDA_HotlistAPI_05 (invalid expiry) | `local.skipTestCases` | expects the hotlist service's own validation error; the hotlist stub does not validate input |
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

**Known local failures** (run, but fail locally until the noted item is
resolved):

| Testcases | Cause |
|---|---|
| TC_IDA_EkycBio_49, TC_IDA_SendOTP_04, TC_IDA_BioAuthDelegatedV2Neg_31 | for the mock-deactivated UIN, IDA returns `IDA-MLC-022`; env returns `IDA-MLC-018` (under check) |

The kyc-exchange v2 `_Decode_` tests need the `init.sql` from
id-authentication#1822 or later, where `mpartner-default-demo-oidc` carries
the same certificate as `config/local/partner-mosip-signed.p12`.

**Known issues for local runs** are in `testCaseSkippedList-local.txt` and are
counted under *Known Issues*.

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