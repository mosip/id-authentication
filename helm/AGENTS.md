# AGENTS.md — `helm/`

> Kubernetes Helm chart for the MOSIP ID Authentication Service.
> Parent guide: [repo root `AGENTS.md`](../AGENTS.md).
> Install/uninstall/restart scripts: [`deploy/ida/`](../deploy/ida/).

---

## 1. Chart

| Chart | Path | Deploys |
|-------|------|---------|
| **ida-auth** | `helm/ida-auth/` | Consolidated ID Authentication Service (`authentication-service`) |

As of Issue #1764, this is the only chart here. `authentication-internal-service` and
`authentication-otp-service` were merged into `authentication-service` (see root
`AGENTS.md` → Architecture), and the `ida-internal` / `ida-otp` charts that used to deploy
them were removed — their image, resource/JVM sizing, and Istio routes were folded into
`ida-auth`.

---

## 2. Key values (`ida-auth/values.yaml`)

| Area | Location / keys |
|---|---|
| Image | `image.repository` (`mosipqa/authentication-service`), `image.tag` |
| Port / health | `springServicePort: 8090`; probes on `/idauthentication/v1/actuator/health` |
| Resources / JVM | `resources`, `additionalResources.javaOpts` — sized for the merged workload (see inline comment); validate under load before trusting in production |
| Istio routing | `istio.match` — most prefixes (`/auth`, `/kyc`, `/swagger-ui`, `/v3/api-docs`, `/actuator`, `/identity-key-binding`, `/vci-exchange`) are open on both `istio-system/public` and `istio-system/internal`; `/internal` and `/otp` carry a per-entry `gateways: [istio-system/internal]` override, matching their original standalone charts (neither was ever public) |

`/idauthentication/v1/internal` is a legacy prefix carried over from the removed
`ida-internal` chart — it does not match any real route on the merged app (the internal
controllers have no `/internal` path segment; see root `AGENTS.md`). Don't rely on it for
anything beyond backward-compatible routing config.

---

## 3. Agent rules

### Do
1. Keep `istio.match` accurate to `authentication-service`'s actual controller routes when routing changes upstream.
2. Update [`deploy/ida/install.sh`](../deploy/ida/install.sh) / [`delete.sh`](../deploy/ida/delete.sh) if a chart is ever added or removed here.
3. Check each `istio.match` entry's own `gateways` override (not just the top-level `istio.gateways`) before assuming a route's exposure.

### Do not
1. Reintroduce `ida-internal` or `ida-otp` as separate charts — the Java service is one deployable now.
2. Assume the presence of an `/internal` or `/otp` URL segment implies network-level restriction; check the `gateways` field on that specific `istio.match` entry instead.

---

## 4. Related install path

[`deploy/ida/install.sh`](../deploy/ida/install.sh) installs `ida-keygen` then `ida-auth`.
[`delete.sh`](../deploy/ida/delete.sh) and [`restart.sh`](../deploy/ida/restart.sh) mirror it.
