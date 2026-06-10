# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Module Identity

- **ArtifactId:** `apitest-auth`
- **Version:** `1.2.1-SNAPSHOT`
- **Main class:** `io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner`
- **Covers:** ID Authentication APIs — biometric auth, OTP auth, demographic auth, eKYC, hotlist, VID/UIN lifecycle

## Common Commands

### Build
```powershell
mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true
```

### Run via JAR
```powershell
java -Dmodules=auth -Denv.user=api-internal.<env_name> -Denv.endpoint=<base_url> -Denv.testLevel=smokeAndRegression -jar target/apitest-auth-1.2.1-jar-with-dependencies.jar
```
`testLevel` values: `smoke` (positive only), `regression`, `smokeAndRegression`

### Run via IDE
Main class: `io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner`  
VM args: `-Dmodules=auth -Denv.user=api-internal.<env_name> -Denv.endpoint=<base_url> -Denv.testLevel=smokeAndRegression`

### Rebuild shared commons (if apitest-commons changed)
```powershell
cd ../../mosip-functional-tests/apitest-commons
mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true
```

## Architecture

### Test Execution Flow
`MosipTestRunner.main()` orchestrates the entire lifecycle:
1. Initializes environment (JAR vs IDE mode), config, and a 120-minute watchdog
2. Sets up Keycloak users, partner keys/certificates, and policies
3. Generates mock biometric test data from `Biometric Devices/` and `resource/Profile/`
4. Loads inter-test dependencies from JSON, then runs `authMasterTestSuite.xml`
5. Cleans up DB records and Keycloak users, then calls `System.exit(0)`

### Suite Structure
`testNgXmlFiles/authMasterTestSuite.xml` includes two phases in order:
- **`authPrerequisiteSuite.xml`** — 17 setup tests: create identity, OIDC client, VID/draft lifecycle, lock state setup. These produce IDs consumed by later tests.
- **`authSuite.xml`** — 20 core auth tests: BioAuth, OtpAuth, DemoAuth, eKYC variants, KYC exchange, hotlist block/unblock, auth transactions.

### YAML-Driven Test Pattern
Every test scenario is defined entirely in YAML under `src/main/resources/ida/<TestName>/`. Java test classes are generic executors — they read the YAML, build the request, call the API, and validate the response. The mapping between YAML file and Java class is declared in the TestNG XML `<parameter name="ymlFile">` and `<test><classes>` respectively.

**YAML fields:**
- `endPoint` — relative URL path (supports `$partnerKeyURL$`, `$ekycPartnerKeyURL$`, etc.)
- `restMethod` — `post`, `get`, `put`, `patch`
- `inputTemplate` / `outputTemplate` — path to `.hbs` Handlebars template for request/response body
- `input` — JSON with dynamic placeholders resolved at runtime
- `output` — expected response fields for assertion
- `idKeyName` — if set, the generated ID from this test is stored under this key for downstream `$ID:<key>$` references

**Key dynamic placeholders in YAML:**
| Placeholder | Resolves to |
|---|---|
| `$TIMESTAMP$` | Current ISO timestamp |
| `$TRANSACTIONID$` | Auto-generated transaction ID |
| `$ID:<testcase_key>$` | ID captured from a prior test's `idKeyName` |
| `$FACE$` / biometric tokens | Mock biometric payload |
| `$partnerKeyURL$` | Partner certificate endpoint segment |
| `$IGNORE$` | Skip validation for this field |

### Java Test Classes (`testscripts/`)
Generic classes wired by the TestNG XML — do not contain test-specific logic:
- `BioAuth` — biometric auth (face/fingerprint/iris)
- `OtpAuthNew` — OTP authentication
- `DemoAuth` — demographic authentication
- `MultiFactorAuthNew` — multi-factor combinations
- `KycExchange` — KYC exchange flows
- `AddIdentity`, `UpdateIdentity` — identity creation/update
- `SimplePost`, `SimplePostForAutoGenId` — generic POST, POST with ID capture
- `GetWithParam`, `GetWithParamForAutoGenId` — generic GET variants
- `PostWithBodyWithOtpGenerate`, `PostWithAutogenIdWithOtpGenerate` — POST flows that trigger OTP internally
- `PutWithPathParamsAndBody`, `PatchWithBodyWithOtpGenerate`, `SimplePatchForAutoGenId` — PUT/PATCH variants

All classes implement TestNG's `ITest`, extend `IdAuthenticationUtil`, and use `@DataProvider` to feed YAML test cases.

### Configuration (`src/main/resources/config/`)
- **`Ida.properties`** — Keycloak URL, PostgreSQL audit/partner DB credentials, client secrets for all microservices, watchdog timeout, UIN/VID delays, component base URLs
- **`application.properties`** — SBI mock device config: ports 4501–4600, biometric profiles (Default/Automatic), device seeds, encryption keystores, MDS error code mappings

### Utilities (`utils/`)
- `IdAuthConfigManager` — loads and exposes `Ida.properties` values
- `IdAuthenticationUtil` — shared auth helpers (key generation, request signing, response parsing)
- `IDAConstants` — module-level constants

## Test Data Organization
`src/main/resources/ida/` contains 50+ directories, one per API/scenario:
- **Prerequisite:** `AddIdentity/`, `OidcClient/`, `CreateVID/`, `GenerateVID/`, `UpdateIdentity/`, `UpdateDraft/`, `PublishDraft/`, `DeactivateUin/`, `RevokeVID/`, etc.
- **Core auth:** `BioAuth/`, `OtpAuth/`, `DemoAuth/`, `EkycBio/`, `EkycOtp/`, `EkycDemo/`, `MultiFactorAuth/`, `BioAuthKycExchangeV2/`
- **Hotlist/lock:** `AuthLock/`, `AuthUnLock/`, `BlockHotlistAPI/`, `UnBlockHotlistAPI/`, `BlockHotlistStatus/`

Each directory contains YAML test definition files (e.g., `BioAuth2.yml`) and `.hbs` Handlebars templates for request/response bodies.

## Mock Biometric Device Data
`Biometric Devices/` contains per-modality mock data (Face, Finger, Iris) including `DigitalId.json`, `DeviceInfo.json`, `DeviceDiscovery.json`, stream images, and keystores. `resource/Profile/` holds Default and Automatic SBI profiles. These are loaded by `MosipTestRunner` at startup to generate biometric payloads for tests.

## Test Reports
Generated in `api-test/testng-report/` after a run. Two sections: prerequisite APIs and core test cases. Columns: Total, Passed, Failed, Skipped, Ignored, Known Issues.