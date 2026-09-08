# IDA Auth

Helm chart for installing IDA Auth service.

## TL;DR

```console
$ helm repo add mosip https://mosip.github.io
$ helm install my-release mosip/ida-auth
```

## Introduction

IDA Auth deploys the consolidated ID Authentication Service (`authentication-service`), which as of Issue #1764 hosts the public auth/OTP partner APIs, the OpenID4VCI/KYC APIs, and the internal-only auth/OTP/auth-transaction/callback APIs (formerly the separate `authentication-otp-service` and `authentication-internal-service` deployments/charts) in a single Spring Boot app. There is no longer a separate `ida-otp` or `ida-internal` chart - see `istio.match` in `values.yaml` for how internal-only routes are still restricted to the internal gateway.

## Prerequisites

- Kubernetes 1.12+
- Helm 3.1.0
- PV provisioner support in the underlying infrastructure
- ReadWriteMany volumes for deployment scaling


