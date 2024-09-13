[![Maven Package upon a push](https://github.com/mosip/id-authentication/actions/workflows/push_trigger.yml/badge.svg?branch=release-1.2.0.1)](https://github.com/mosip/id-authentication/actions/workflows/push_trigger.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mosip_id-authentication&id=mosip_id-authentication&branch=release-1.2.0.1&metric=alert_status)](https://sonarcloud.io/dashboard?id=mosip_id-authentication&branch=release-1.2.0.1)

# ID-Authentication 

## Overview
This repository contains source code and design documents for MOSIP ID Authentication which is the server-side module to manage [ID Authentication](https://docs.mosip.io/1.2.0/modules/id-authentication-services). The modules exposes API endpoints.  

## Databases
Refer to [SQL scripts](db_scripts).

## Build & run (for developers)
The project requires JDK 1.11. 
1. Build and install:
    ```
    $ cd kernel
    $ mvn install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true
    ```
1. Build Docker for a service:
    ```
    $ cd <service folder>
    $ docker build -f Dockerfile
    ```

## Configuration
Refer to the [configuration guide](docs/configuration.md).

## Deployment in K8 cluster with other MOSIP services:
### Pre-requisites
* Set KUBECONFIG variable to point to existing K8 cluster kubeconfig file:
    ```
    export KUBECONFIG=~/.kube/<k8s-cluster.config>
    ```
### Install
  ```
    $ cd deploy
    $ ./install.sh
   ```
### Delete
  ```
    $ cd deploy
    $ ./delete.sh
   ```
### Restart
  ```
    $ cd deploy
    $ ./restart.sh
   ```

## To deploy Auth apitestrig within k8s cluster:
### Install
  ```
    $ cd ./apitest/deploy/auth-apitestrig
    $ ./install.sh
   ```
### Delete
  ```
    $ cd ./apitest/deploy/auth-apitetsrig
    $ ./delete.sh
   ```

To deploy Auth on Kubernetes cluster using Dockers refer to [Sandbox Deployment](https://docs.mosip.io/1.2.0/deployment/sandbox-deployment).
To deploy Auth-apitestrig within the cluster refer to [Auth-apitestrig Deployment](https://github.com/mosip/id-authentication/tree/develop/apitest/deploy/auth-apitestrig/README.md) 

## Test
Automated functional tests available in [Functional Tests repo](https://github.com/mosip/mosip-functional-tests).

## APIs
API documentation is available [here](https://mosip.github.io/documentation/).

## License
This project is licensed under the terms of [Mozilla Public License 2.0](LICENSE).
