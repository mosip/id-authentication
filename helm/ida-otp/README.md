# IDA Auth

Helm chart for installing IDA Auth service.

## TL;DR

```console
$ helm repo add mosip https://mosip.github.io
$ helm install my-release mosip/ida-otp
```

## Introduction

IDA Auth is  part of the IDA module, but has a separate Helm chart so as to install and manage it in a completely indepedent namespace.

## Prerequisites

- Kubernetes 1.12+
- Helm 3.1.0
- PV provisioner support in the underlying infrastructure
- ReadWriteMany volumes for deployment scaling


