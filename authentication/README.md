## MOSIP ID Authentication

ID Authentication (IDA) is the authentication module of MOSIP, used to authenticate Individuals using their UIN/VID, via a Partner. 

List of authentication types supported by MOSIP are - 
1. OTP Authentication
2. Demographic Authentication
3. Biometric Authentication (includes Fingerprint, IRIS and Face)

Refer wiki page for [ID Authentication API](https://github.com/mosip/mosip-docs/wiki/ID-Authentication-API)   

Services included in ID Authentication module are -

1.[Internal Authentication Service](./authentication-internal-service/README.md)
2.[OTP Service](./authentication-otp-service/README.md)
3.[Authentication service](./authentication-service/README.md)

## Databases
Before starting the local setup, execute the required SQL scripts to initialize the database.
All database SQL scripts are available in the [db_scripts](../db_scripts) directory.

## Local Setup
The project can be set up in two ways:

1. [Local Setup (for Development or Contribution)](#local-setup-for-development-or-contribution)
2. [Local Setup with Docker (Easy Setup for Demos)](#local-setup-with-docker-easy-setup-for-demos)

### Prerequisites
Install or configure the following:

- **JDK**: 21.0.3
- **Maven**: 3.9.6
- **Docker**: Latest stable version
- **PostgreSQL**: 16.0
- **Keycloak**: [Check here](https://github.com/mosip/keycloak/tree/master)

**Configuration**
- Id-Authentication module uses the following configuration files that are accessible in this [repository](https://github.com/mosip/mosip-config/tree/master).
  Please refer to the required released tagged version for configuration.
    - [application-default.properties](https://github.com/mosip/mosip-config/blob/master/application-default.properties) : Contains common configurations which are required across MOSIP modules.
    - [id-authentication-default.properties](https://github.com/mosip/mosip-config/blob/master/id-authentication-default.properties) : Contains common configurations which are required across IDA services.

## Installation

### Local Setup (for Development or Contribution)
1. Make sure the config server is running. For detailed instructions on setting up and running the, refer to the 
- [Authentication service Server Setup Guide](https://docs.mosip.io/1.2.0/id-lifecycle-management/identity-verification/id-authentication-services/id-authentication-service-developer-guide)
- [Id authentication otp serivce Server setup guide](https://docs.mosip.io/1.2.0/id-lifecycle-management/identity-verification/id-authentication-services/id-authentication-otp-service-developer-guide)
- [Id authentication internal serivce Server setup guide](https://docs.mosip.io/1.2.0/id-lifecycle-management/identity-verification/id-authentication-services/id-authentication-internal-service-developer-guide)
2. Clone the repository:
```text
git clone <repo-url>
```
```text
cd authentication
```
3. Build the project using Maven :
```text
mvn clean install -Dmaven.javadoc.skip=true -Dgpg.skip=true
```

4. Start the application:
    - Click the Run button in your IDE.

5. Verify Swagger is accessible at: http://localhost:8080/idauthentication/v1/swagger-ui/index.html?configUrl=/idauthentication/v1/v3/api-docs/swagger-config


### Local Setup with Docker (Easy Setup for Demos)
#### Option 1: Pull from Docker Hub

Recommended for users who want a quick, ready-to-use setup — testers, students, and external users.

Pull the latest pre-built images from Docker Hub using the following commands:
```text
docker pull mosipid/id-authentication-service:1.3.0
```

#### Option 2: Build Docker Images Locally

Recommended for contributors or developers who want to modify or build the services from source.

1. Clone and build the project:

```text
git clone <repo-url>
```
```text
cd authentication
```
```text
mvn clean install -Dmaven.javadoc.skip=true -Dgpg.skip=true
```
2. Navigate to each service directory and build the Docker image:
```text
cd authentication/<service-directory>
```
```text
docker build -t <service-name> .
```
#### Running the Services

Start each service using Docker:

```text
docker run -d -p <port>:<port> --name <service-name> <service-name>
```
#### Verify Installation

Check that all containers are running:

```text
docker ps
```
Access the services at `http://localhost:<port>` using the port mappings listed above.

## Deployment
### Kubernetes
To deploy id-authentication services on a Kubernetes cluster, refer to the [Sandbox Deployment Guide](https://docs.mosip.io/1.2.0/deploymentnew/v3-installation).

## Documentation
API endpoints and mock server details are available via Stoplight
and Swagger documentation: API documentation is available [here](https://mosip.stoplight.io/docs/id-authentication/cv3ijedwvqu6s-kyc-auth-api)

### Product Documentation

To learn more about resident service from a functional perspective and use case scenarios, refer to our main documentation: [Click here](https://docs.mosip.io/1.2.0/id-lifecycle-management/identity-verification/id-authentication)

## Testing

Automated functional tests are available in the [Functional tests](../api-test).

## Contribution & Community

• To learn how you can contribute code to this application, [click here](https://docs.mosip.io/1.2.0/community/code-contributions).

• If you have questions or encounter issues, visit the [MOSIP Community](https://community.mosip.io/) for support.

• For any GitHub issues: [Report here](https://github.com/mosip/id-authentication/issues)

## License
This project is licensed under the terms of [Mozilla Public License 2.0](LICENSE).
