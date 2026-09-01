# AGENTS.md — `deploy/`

> Kubernetes install/uninstall scripts for the MOSIP ID Authentication Service.
> Chart source: [`helm/AGENTS.md`](../helm/AGENTS.md).

---

## 1. Layout

```
deploy/
├── copy_cm_func.sh      # Shared helper: copies a configmap/secret between namespaces
├── ida/                 # Installs the ida-auth chart into the `ida` namespace
│   ├── install.sh
│   ├── delete.sh
│   ├── restart.sh
│   └── certs_upload/    # Postman collection to upload IDA's cert to Keymanager (partner onboarding)
└── ida-apitestrig/      # Installs the IDA API test rig (smoke/regression cronjobs) into `ida`
    ├── install.sh
    ├── delete.sh
    └── values.yaml
```

## 2. `deploy/ida/`

Installs `ida-keygen` then `ida-auth` (the single consolidated ID Authentication Service chart —
see [`helm/AGENTS.md`](../helm/AGENTS.md)) into the `ida` namespace, copying required configmaps
via `copy_cm_func.sh` first. As of Issue #1764, this is the **only** service chart installed here —
`install.sh`/`delete.sh` no longer reference `ida-internal` or `ida-otp`, since those charts were
removed and folded into `ida-auth`.

- `install.sh [kubeconfig]` — prompts for public-domain/SSL and volume options, then installs.
- `delete.sh [kubeconfig]` — prompts for confirmation, then uninstalls `ida-keygen` and `ida-auth`.
- `restart.sh [kubeconfig]` — rolling-restarts all deployments in the `ida` namespace.
- `certs_upload/` — manual step after install: upload IDA's cert to Keymanager via the included
  Postman collection (partner onboarding is otherwise automatic).

## 3. `deploy/ida-apitestrig/`

Installs the API test rig (smoke/regression k8s cronjobs against the deployed IDA APIs) into the
same `ida` namespace as `deploy/ida/` (its README's `-n apitestrig` example in the manual-run
section is stale — the cronjobs actually run in `ida`, matching `install.sh`'s `NS=ida`).
Independent of the `ida-auth` chart itself — review `values.yaml` to enable the modules you want
tested before installing. Run a specific cronjob on demand via
`kubectl create job --from=cronjob/<name>` (see its `README.md`).

## 4. Agent rules

### Do
1. If a chart under `helm/` is ever added or removed, update `deploy/ida/install.sh` and
   `delete.sh` to match (this is what happened when `ida-internal`/`ida-otp` were removed).
2. Reuse `copy_cm_func.sh` for any new configmap/secret copy needs rather than duplicating
   `kubectl` plumbing inline.

### Do not
1. Don't add back `ida-internal`/`ida-otp` install/delete blocks — the Java service is one
   deployable now (`authentication-service`, chart `ida-auth`).
2. Don't hardcode namespace names other than `ida` / `apitestrig`-scoped values already used by
   these scripts without checking `copy_cm_func.sh` usage sites for consistency.
