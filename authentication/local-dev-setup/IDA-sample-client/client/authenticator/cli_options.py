import base64
import getpass
import json
from datetime import datetime, timezone
from pathlib import Path

from authenticator import MOSIPAuthenticator


BASE_DIR = Path(__file__).resolve().parents[2]

DEFAULT_CONSENT_CLAIMS = "name,address,gender,birthdate,email,phone"
DEFAULT_LOCALES = "eng"
DEFAULT_RESP_TYPE = "JWT"
DEFAULT_VC_FORMAT = "ldp_vc"
UNVERIFIED_CLAIM_NAMES = "sub,name,address,individual_id,gender,birthdate,email,phone_number,picture"

# verifiedClaimsMetadata of kyc-auth (v2) responses of this session, keyed by transaction id
_VERIFIED_CLAIMS_METADATA = {}
_VERIFICATION_LABELS = {"trust_framework": "trust_framework", "verification_process": "verification_process", "time": "verified on"}

BIOMETRIC_OPTIONS = [
    ("Face", "Face", ""),
    ("Finger_Left IndexFinger", "Finger", "Left IndexFinger"),
    ("Finger_Left LittleFinger", "Finger", "Left LittleFinger"),
    ("Finger_Left MiddleFinger", "Finger", "Left MiddleFinger"),
    ("Finger_Left RingFinger", "Finger", "Left RingFinger"),
    ("Finger_Left Thumb", "Finger", "Left Thumb"),
    ("Finger_Right IndexFinger", "Finger", "Right IndexFinger"),
    ("Finger_Right LittleFinger", "Finger", "Right LittleFinger"),
    ("Finger_Right MiddleFinger", "Finger", "Right MiddleFinger"),
    ("Finger_Right RingFinger", "Finger", "Right RingFinger"),
    ("Finger_Right Thumb", "Finger", "Right Thumb"),
    ("Iris_Left", "Iris", "Left"),
    ("Iris_Right", "Iris", "Right"),
]


def _print_readable_response(raw_response: str) -> None:
    try:
        parsed_response = json.loads(raw_response)
    except Exception:
        print("\nResponse (raw):")
        print(raw_response)
        return

    transaction_id = (
        parsed_response.get("transactionID")
        or parsed_response.get("transactionId")
        or parsed_response.get("transaction_id")
    )
    response_data = parsed_response.get("response") or {}
    auth_status = response_data.get("authStatus")
    auth_token = response_data.get("authToken")
    masked_mobile = response_data.get("maskedMobile")
    masked_email = response_data.get("maskedEmail")

    print("\nResponse:")
    print(f"transaction_id: {transaction_id}")
    if auth_status is not None:
        print(f"response.authStatus: {auth_status}")
    if auth_token is not None:
        print(f"response.authToken: {auth_token}")
    if masked_mobile is not None:
        print(f"response.maskedMobile: {masked_mobile}")
    if masked_email is not None:
        print(f"response.maskedEmail: {masked_email}")

    errors = parsed_response.get("errors") or []
    if isinstance(errors, list) and errors:
        print("errors:")
        for error in errors:
            error_code = error.get("errorCode")
            error_message = error.get("errorMessage")
            print(f"- errorCode: {error_code}, errorMessage: {error_message}")
    else:
        print("errors: []")


def _print_kyc_response(raw_response: str) -> None:
    try:
        parsed_response = json.loads(raw_response)
    except Exception:
        print("\nResponse (raw):")
        print(raw_response)
        return

    response_data = parsed_response.get("response") or {}

    print(f"kycStatus: {response_data.get('kycStatus')}")
    print(f"authToken: {response_data.get('authToken')}")
    print(f"transactionID: {parsed_response.get('transactionID')}")
    print(f"identity: {response_data.get('identity')}")
    print(f"sessionKey: {response_data.get('sessionKey')}")


def _print_kyc_auth_response(raw_response: str, kyc_v2: bool = False) -> None:
    try:
        parsed_response = json.loads(raw_response)
    except Exception:
        print("\nResponse (raw):")
        print(raw_response)
        return

    response_data = parsed_response.get("response") or {}

    print("\nResponse:")
    print(f"transactionID: {parsed_response.get('transactionID')}")
    print(f"response.kycStatus: {response_data.get('kycStatus')}")
    print(f"response.kycToken: {response_data.get('kycToken')}")
    print(f"response.authToken: {response_data.get('authToken')}")
    if kyc_v2:
        print(f"response.verifiedClaimsMetadata: {response_data.get('verifiedClaimsMetadata')}")
        _VERIFIED_CLAIMS_METADATA[str(parsed_response.get("transactionID"))] = response_data.get("verifiedClaimsMetadata")
    _print_errors(parsed_response)


def _print_errors(parsed_response: dict) -> None:
    errors = parsed_response.get("errors") or []
    if isinstance(errors, list) and errors:
        print("errors:")
        for error in errors:
            print(f"- errorCode: {error.get('errorCode')}, errorMessage: {error.get('errorMessage')}")
    else:
        print("errors: []")


MAIN_MENU_OPTIONS = {
    "1": {"label": "Auth", "use_ekyc": False, "use_kyc_auth": False, "use_key_binding": False, "show_submenu": True},
    "2": {"label": "eKYC", "use_ekyc": True, "use_kyc_auth": False, "use_key_binding": False, "show_submenu": True},
    "3": {"label": "kyc-auth & kyc-exchange/vci-exchange (v1)", "use_ekyc": False, "use_kyc_auth": True, "use_key_binding": False, "show_submenu": True},
    "4": {"label": "kyc-auth & kyc-exchange (v2)", "use_ekyc": False, "use_kyc_auth": True, "use_key_binding": False, "kyc_v2": True, "show_submenu": True},
    "5": {"label": "Identity key binding", "use_ekyc": False, "use_kyc_auth": False, "use_key_binding": True, "show_submenu": True},
}


def _print_main_menu() -> None:
    print("\nMain Menu:")
    print("1. Auth (True/False)")
    print("2. eKYC")
    print("3. kyc-auth & kyc-exchange/vci-exchange (v1)")
    print("4. kyc-auth & kyc-exchange (v2)")
    print("5. Identity key binding")
    print("0. Exit")


def _print_submenu(category_label: str, option_labels: list) -> None:
    print(f"\n{category_label} - Choose an option:")
    for index, label in enumerate(option_labels, start=1):
        print(f"{index}. {label}")
    print("0. Back to main menu")


def _get_submenu_actions(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool, use_kyc_auth: bool, use_key_binding: bool, kyc_v2: bool = False) -> list:
    """Returns the (label, action) list of a submenu. Demo based options are not offered for kyc-auth; password and key binded token are kyc-auth only."""
    demo_auth = ("Demo auth", lambda: _run_demo_auth(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding))
    send_otp = ("Send OTP request", lambda: _run_send_otp(mosip_authenticator, use_kyc_auth, use_key_binding))
    otp_auth = ("OTP auth", lambda: _run_otp_auth(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding, kyc_v2))
    demo_otp_auth = ("Demo + OTP", lambda: _run_demo_otp_auth(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding))
    bio_auth = ("Biometric auth", lambda: _run_bio_auth(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding, kyc_v2))

    if use_kyc_auth:
        kyc_auth_actions = [send_otp, otp_auth, bio_auth,
                            ("Password", lambda: _run_password_auth(mosip_authenticator, kyc_v2)),
                            ("Key binded token", lambda: _run_key_binded_token_auth(mosip_authenticator, kyc_v2))]
        if kyc_v2:
            return kyc_auth_actions + [("kyc-exchange", lambda: _run_kyc_exchange_v2(mosip_authenticator))]
        return kyc_auth_actions + [("kyc-exchange", lambda: _run_kyc_exchange(mosip_authenticator)),
                                   ("vci-exchange", lambda: _run_vci_exchange(mosip_authenticator))]
    return [demo_auth, send_otp, otp_auth, demo_otp_auth, bio_auth]


def _print_key_binding_response(mosip_authenticator: MOSIPAuthenticator, raw_response: str) -> None:
    try:
        parsed_response = json.loads(raw_response)
    except Exception:
        print("\nResponse (raw):")
        print(raw_response)
        return

    response_data = parsed_response.get("response") or {}

    print("\nResponse:")
    print(f"transactionID: {parsed_response.get('transactionID')}")
    print(f"response.bindingAuthStatus: {response_data.get('bindingAuthStatus')}")
    print(f"response.authToken: {response_data.get('authToken')}")
    print(f"response.identityCertificate: {response_data.get('identityCertificate')}")
    stored_path = mosip_authenticator.last_bound_p12_path
    print(f"Key + certificate stored in: {stored_path}" if stored_path else "Key + certificate not stored.")
    _print_errors(parsed_response)


def _print_auth_result(mosip_authenticator: MOSIPAuthenticator, raw_response: str, use_ekyc: bool, use_kyc_auth: bool, use_key_binding: bool = False, kyc_v2: bool = False) -> None:
    if use_key_binding:
        _print_key_binding_response(mosip_authenticator, raw_response)
    elif use_kyc_auth:
        _print_kyc_auth_response(raw_response, kyc_v2)
    elif use_ekyc:
        _print_kyc_response(raw_response)
    else:
        _print_readable_response(raw_response)


def _get_sample_files(samples_dir: Path) -> list[Path]:
    return sorted([p for p in samples_dir.iterdir() if p.is_file() and p.suffix == ".json"])


def _select_biometric() -> tuple[str, str, str] | None:
    print("\nAvailable biometrics:")
    for index, (label, _, _) in enumerate(BIOMETRIC_OPTIONS, start=1):
        print(f"{index}. {label}")

    while True:
        choice = input("Select a biometric number (or 0 to cancel): ").strip()
        if choice == "0":
            return None
        if choice.isdigit():
            idx = int(choice)
            if 1 <= idx <= len(BIOMETRIC_OPTIONS):
                return BIOMETRIC_OPTIONS[idx - 1]
        print("Invalid selection. Please enter a valid number.")


def _select_sample_file(samples_dir: Path) -> Path | None:
    if not samples_dir.exists():
        print(f"Samples folder not found: {samples_dir}")
        return None

    sample_files = _get_sample_files(samples_dir)
    if not sample_files:
        print(f"No JSON files found in samples folder: {samples_dir}")
        return None

    print("\nAvailable sample files:")
    for index, sample_file in enumerate(sample_files, start=1):
        print(f"{index}. {sample_file.name}")

    while True:
        choice = input("Select a file number (or 0 to cancel): ").strip()
        if choice == "0":
            return None
        if choice.isdigit():
            idx = int(choice)
            if 1 <= idx <= len(sample_files):
                return sample_files[idx - 1]
        print("Invalid selection. Please enter a valid number.")


def _run_demo_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False) -> None:
    sample_file = _select_sample_file(BASE_DIR / "samples")
    if not sample_file:
        return

    try:
        with sample_file.open("r", encoding="utf-8") as file:
            json_data = json.load(file)
        auth_resp = mosip_authenticator.do_auth(json_data, use_ekyc=use_ekyc, use_kyc_auth=use_kyc_auth, use_key_binding=use_key_binding)
        _print_auth_result(mosip_authenticator, auth_resp, use_ekyc, use_kyc_auth, use_key_binding)
    except Exception as exc:
        print(f"Failed to run demo auth: {exc}")


def _run_send_otp(mosip_authenticator: MOSIPAuthenticator, use_kyc_auth: bool = False, use_key_binding: bool = False) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    try:
        otp_resp = mosip_authenticator.send_otp_request(idvid, use_kyc_auth=use_kyc_auth, use_key_binding=use_key_binding)
        _print_readable_response(otp_resp)
    except Exception as exc:
        print(f"Failed to send OTP request: {exc}")


def _run_otp_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False, kyc_v2: bool = False) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    otp = input("Enter OTP received (Check docker compose logs for OTP): ").strip()
    if not otp:
        print("OTP cannot be empty.")
        return

    transaction_id = input("Enter Transaction Id from OTP response: ").strip()
    if not transaction_id:
        print("Transaction Id cannot be empty.")
        return

    try:
        otp_auth_resp = mosip_authenticator.do_otp_auth(idvid, otp, transaction_id, use_ekyc=use_ekyc, use_kyc_auth=use_kyc_auth, use_key_binding=use_key_binding, kyc_v2=kyc_v2)
        _print_auth_result(mosip_authenticator, otp_auth_resp, use_ekyc, use_kyc_auth, use_key_binding, kyc_v2)
    except Exception as exc:
        print(f"Failed to run OTP auth: {exc}")


def _run_bio_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False, kyc_v2: bool = False) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    selected_biometric = _select_biometric()
    if not selected_biometric:
        return

    label, bio_type, bio_sub_type = selected_biometric

    try:
        bio_auth_resp = mosip_authenticator.do_bio_auth(idvid, bio_type, bio_sub_type, use_ekyc=use_ekyc, use_kyc_auth=use_kyc_auth, use_key_binding=use_key_binding, kyc_v2=kyc_v2)
        print(f"\nSelected biometric: {label}")
        _print_auth_result(mosip_authenticator, bio_auth_resp, use_ekyc, use_kyc_auth, use_key_binding, kyc_v2)
    except Exception as exc:
        print(f"Failed to run biometric auth: {exc}")


def _run_password_auth(mosip_authenticator: MOSIPAuthenticator, kyc_v2: bool = False) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    password = getpass.getpass("Enter password (input hidden): ")
    if not password:
        print("Password cannot be empty.")
        return

    try:
        resp = mosip_authenticator.do_password_auth(idvid, password, kyc_v2)
        _print_kyc_auth_response(resp, kyc_v2)
    except Exception as exc:
        print(f"Failed to run password auth: {exc}")


def _run_key_binded_token_auth(mosip_authenticator: MOSIPAuthenticator, kyc_v2: bool = False) -> None:
    idvid = input("Enter UIN/VID (must have a key bound via Identity key binding): ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    try:
        resp = mosip_authenticator.do_key_binded_token_auth(idvid, kyc_v2)
        _print_kyc_auth_response(resp, kyc_v2)
    except Exception as exc:
        print(f"Failed to run key binded token auth: {exc}")


def _run_demo_otp_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    otp = input("Enter OTP received (Check docker compose logs for OTP): ").strip()
    if not otp:
        print("OTP cannot be empty.")
        return

    transaction_id = input("Enter Transaction Id from OTP response: ").strip()
    if not transaction_id:
        print("Transaction Id cannot be empty.")
        return

    sample_file = _select_sample_file(BASE_DIR / "samples")
    if not sample_file:
        return

    try:
        with sample_file.open("r", encoding="utf-8") as file:
            json_data = json.load(file)
        demo_otp_resp = mosip_authenticator.do_demo_otp_auth(json_data, idvid, otp, transaction_id, use_ekyc=use_ekyc, use_kyc_auth=use_kyc_auth, use_key_binding=use_key_binding)
        _print_auth_result(mosip_authenticator, demo_otp_resp, use_ekyc, use_kyc_auth, use_key_binding)
    except Exception as exc:
        print(f"Failed to run Demo + OTP auth: {exc}")


def _run_kyc_exchange(mosip_authenticator: MOSIPAuthenticator) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    transaction_id = input("Enter Transaction Id from kyc-auth request: ").strip()
    if not transaction_id:
        print("Transaction Id cannot be empty.")
        return

    kyc_token = input("Enter kycToken from kyc-auth response: ").strip()
    if not kyc_token:
        print("kycToken cannot be empty.")
        return

    claims_input = input(f"Enter consented claims, comma separated [{DEFAULT_CONSENT_CLAIMS}]: ").strip() or DEFAULT_CONSENT_CLAIMS
    locales_input = input(f"Enter locales, comma separated [{DEFAULT_LOCALES}]: ").strip() or DEFAULT_LOCALES
    resp_type = input(f"Enter respType [{DEFAULT_RESP_TYPE}]: ").strip() or DEFAULT_RESP_TYPE
    consent_obtained = [c.strip() for c in claims_input.split(",") if c.strip()]
    locales = [l.strip() for l in locales_input.split(",") if l.strip()]

    try:
        resp = mosip_authenticator.send_kyc_exchange_request(idvid, kyc_token, consent_obtained, locales, transaction_id, resp_type)
        _print_kyc_exchange_response(resp)
    except Exception as exc:
        print(f"Failed to run kyc-exchange: {exc}")


def _print_kyc_exchange_response(raw_response: str) -> None:
    try:
        parsed_response = json.loads(raw_response)
    except Exception:
        print("\nResponse (raw):")
        print(raw_response)
        return

    response_data = parsed_response.get("response") or {}
    print("\nResponse:")
    print(f"transactionID: {parsed_response.get('transactionID')}")
    encrypted_kyc = response_data.get('encryptedKyc')
    print(f"response.encryptedKyc: {encrypted_kyc}")
    _print_decoded_kyc(encrypted_kyc)
    _print_errors(parsed_response)


def _decode_jwt_part(part: str):
    return json.loads(base64.urlsafe_b64decode(part + "=" * (-len(part) % 4)))


def _print_decoded_kyc(encrypted_kyc) -> None:
    """Prints the header and payload of encryptedKyc when it is a signed JWT (respType JWT). A JWE cannot be decoded here."""
    parts = str(encrypted_kyc or "").split(".")
    if len(parts) == 5:
        print("\nencryptedKyc is an encrypted JWE; it cannot be decoded here.")
    elif len(parts) == 3:
        try:
            print("\nDecoded encryptedKyc (JWT):")
            print(f"header: {json.dumps(_decode_jwt_part(parts[0]))}")
            print(f"payload: {json.dumps(_decode_jwt_part(parts[1]), indent=2)}")
        except Exception:
            print("encryptedKyc could not be decoded as a JWT.")


def _print_key_binding_warning(mosip_authenticator: MOSIPAuthenticator) -> None:
    print("\nNOTE: Bound keys (p12 files) are kept in new-keys/bound/ and reused per UIN/VID.")
    print("If the server was restarted (e.g. docker compose down/up) its data is gone, so delete any")
    print("previously generated keys from new-keys/bound/ before using key binding again.")
    print("They are deleted automatically when you exit the application with option 0.")
    existing = len(mosip_authenticator.key_binding_util.list_p12_files())
    if existing:
        print(f"WARNING: {existing} existing bound key file(s) found in new-keys/bound/.")


def _run_vci_exchange(mosip_authenticator: MOSIPAuthenticator) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    transaction_id = input("Enter Transaction Id from kyc-auth request: ").strip()
    if not transaction_id:
        print("Transaction Id cannot be empty.")
        return

    vc_auth_token = input("Enter authToken from kyc-auth response (vcAuthToken): ").strip()
    if not vc_auth_token:
        print("vcAuthToken cannot be empty.")
        return

    vc_format = input(f"Enter vcFormat [{DEFAULT_VC_FORMAT}]: ").strip() or DEFAULT_VC_FORMAT
    locales_input = input(f"Enter locales, comma separated [{DEFAULT_LOCALES}]: ").strip() or DEFAULT_LOCALES
    locales = [l.strip() for l in locales_input.split(",") if l.strip()]

    try:
        resp = mosip_authenticator.send_vci_exchange_request(
            idvid, vc_auth_token, vc_format, locales, transaction_id)
        _print_vci_exchange_response(resp)
    except Exception as exc:
        print(f"Failed to run vci-exchange: {exc}")


def _print_vci_exchange_response(raw_response: str) -> None:
    try:
        parsed_response = json.loads(raw_response)
    except Exception:
        print("\nResponse (raw):")
        print(raw_response)
        return

    credential = (parsed_response.get("response") or {}).get("verifiableCredentials")
    if not credential:
        print("\nResponse (raw JSON):")
        print(json.dumps(parsed_response, indent=2))
        _print_errors(parsed_response)
        return

    print("\nResponse:")
    print(f"transactionID: {parsed_response.get('transactionID')}")
    print(f"responseTime: {parsed_response.get('responseTime')}")

    print("verifiableCredentials:")
    print(json.dumps(credential, indent=2))

    _print_errors(parsed_response)


def _extract_verified_claims(metadata) -> dict:
    """Returns {claim name: [verification metadata entries]} from kyc-auth verifiedClaimsMetadata.

    Accepts a JSON string or an already parsed value, shaped either as a list of
    {"verification": {...}, "claims": [...]} entries or as a {claim name: metadata} map.
    """
    if isinstance(metadata, str):
        try:
            metadata = json.loads(metadata)
        except Exception:
            return {}

    claims = {}
    if isinstance(metadata, dict) and isinstance(metadata.get("verified_claims", metadata.get("verifiedClaims")), (list, dict)):
        metadata = metadata.get("verified_claims", metadata.get("verifiedClaims"))
    if isinstance(metadata, list):
        for entry in metadata:
            if not isinstance(entry, dict):
                continue
            names = entry.get("claims") or []
            for name in (names.keys() if isinstance(names, dict) else names):
                claims.setdefault(str(name), []).append(entry.get("verification") or {})
    elif isinstance(metadata, dict):
        for name, value in metadata.items():
            claims[str(name)] = value if isinstance(value, list) else [value]
    return claims


def _newest_verification_age_seconds(verifications: list):
    """Age in seconds of the newest recorded verification (times are taken as UTC), or None."""
    now = datetime.now(timezone.utc)
    ages = []
    for verification in verifications or []:
        try:
            verified_on = datetime.fromisoformat(str(verification.get('time')).replace('Z', '')).replace(tzinfo=timezone.utc)
            ages.append(int((now - verified_on).total_seconds()))
        except Exception:
            continue
    return min(ages) if ages else None


def _prompt_verification_set(claim: str, set_number: int, verifications: list = None) -> dict:
    """Asks for one verification set: trust_framework + verification_process + max_time, always given together."""
    print(f"  Verification set {set_number} for '{claim}' (enter the three values together):")
    while True:
        trust_framework = input("    trust_framework (required): ").strip()
        if trust_framework:
            break
        print("    trust_framework is required.")
    verification_process = input("    verification_process (Enter to skip): ").strip()
    while True:
        age = _newest_verification_age_seconds(verifications)
        if age is not None:
            print(f"    (newest IDA verification of '{claim}' is {age} seconds old; a smaller max_time will exclude it)")
        max_time = input("    max_time in seconds (Enter to skip): ").strip()
        if not max_time or max_time.isdigit():
            break
        print("    max_time must be a number.")

    verification = {"trust_framework": {"value": trust_framework}}
    if verification_process:
        verification["verification_process"] = {"value": verification_process}
    if max_time:
        verification["time"] = {"max_age": int(max_time)}
    return verification


def _prompt_verification_sets(claim: str, verifications: list = None) -> list:
    """A claim can be requested with several verification sets, e.g. eidas/ver-proc-01 and de_aml/ver-proc-007."""
    verification_sets = []
    while True:
        verification_sets.append(_prompt_verification_set(claim, len(verification_sets) + 1, verifications))
        if input(f"  Add another verification set for '{claim}'? (y/N): ").strip().lower() != "y":
            return verification_sets


def _collect_verified_claims(metadata) -> list:
    available = _extract_verified_claims(metadata)
    if available:
        print("\nVerified claims available from kyc-auth (verifiedClaimsMetadata):")
        names = list(available)
        for index, name in enumerate(names, start=1):
            print(f"{index}. {name}")
            for number, verification in enumerate(available[name], start=1):
                if isinstance(verification, dict):
                    details = ", ".join(f"{_VERIFICATION_LABELS.get(key, key)}: {value}" for key, value in verification.items())
                else:
                    details = str(verification)
                print(f"     verification {number} -> {details}")
            print()
    else:
        names = []
        print("\nNo verifiedClaimsMetadata found for this transaction id (kyc-auth v2 not run in this session or no verified claims).")
        if metadata:
            print(f"verifiedClaimsMetadata: {metadata}")
        print("You can still type verified claim names manually.")

    claim_filters = {}
    while True:
        choice = input("\nVerified claim to request - number or name (Enter when done): ").strip()
        if not choice:
            break
        if choice.isdigit() and 1 <= int(choice) <= len(names):
            claim = names[int(choice) - 1]
        elif choice in available or not names:
            claim = choice
        else:
            print("Invalid selection. Pick a claim from the list above.")
            continue
        claim_filters[claim] = _prompt_verification_sets(claim, available.get(claim))
        print(f"  Added '{claim}'. Pick another claim or press Enter to continue.")

    # claims that share the same verification set go into one verifiedConsentedClaims entry
    grouped = {}
    for claim, verification_sets in claim_filters.items():
        for verification in verification_sets:
            entry = grouped.setdefault(json.dumps(verification, sort_keys=True), {"verification": verification, "claims": {}})
            entry["claims"][claim] = None
    return list(grouped.values())


def _collect_unverified_claims() -> dict:
    print(f"\nUnverified claims (no filters). Available: {UNVERIFIED_CLAIM_NAMES}")
    claims_input = input("Enter unverified claims, comma separated (Enter for none): ").strip()
    return {c.strip(): None for c in claims_input.split(",") if c.strip()}


def _run_kyc_exchange_v2(mosip_authenticator: MOSIPAuthenticator) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    transaction_id = input("Enter Transaction Id from kyc-auth request: ").strip()
    if not transaction_id:
        print("Transaction Id cannot be empty.")
        return

    kyc_token = input("Enter kycToken from kyc-auth response: ").strip()
    if not kyc_token:
        print("kycToken cannot be empty.")
        return

    verified_claims = _collect_verified_claims(_VERIFIED_CLAIMS_METADATA.get(transaction_id))
    unverified_claims = _collect_unverified_claims()
    if not verified_claims and not unverified_claims:
        print("Select at least one verified or unverified claim.")
        return

    locales_input = input(f"Enter locales, comma separated [{DEFAULT_LOCALES}]: ").strip() or DEFAULT_LOCALES
    locales = [l.strip() for l in locales_input.split(",") if l.strip()]
    resp_type = input(f"Enter respType [{DEFAULT_RESP_TYPE}]: ").strip() or DEFAULT_RESP_TYPE

    print("\nClaims to be requested:")
    print(f"verifiedConsentedClaims: {json.dumps(verified_claims, indent=2)}")
    print(f"unVerifiedConsentedClaims: {json.dumps(unverified_claims)}")

    try:
        resp = mosip_authenticator.send_kyc_exchange_v2_request(
            idvid, kyc_token, verified_claims, unverified_claims, locales, transaction_id, resp_type)
        _print_kyc_exchange_response(resp)
    except Exception as exc:
        print(f"Failed to run kyc-exchange (v2): {exc}")


def _run_submenu(mosip_authenticator: MOSIPAuthenticator, category_label: str, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False, kyc_v2: bool = False) -> None:
    if use_key_binding:
        _print_key_binding_warning(mosip_authenticator)
    actions = _get_submenu_actions(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding, kyc_v2)
    while True:
        _print_submenu(category_label, [label for label, _ in actions])
        selected_option = input("Enter option: ").strip()

        if selected_option == "0":
            return
        if selected_option.isdigit() and 1 <= int(selected_option) <= len(actions):
            actions[int(selected_option) - 1][1]()
        else:
            print("Invalid option. Please choose from the menu.")


def run_cli(mosip_authenticator: MOSIPAuthenticator) -> None:
    while True:
        _print_main_menu()
        selected_option = input("Enter option: ").strip()

        if selected_option in MAIN_MENU_OPTIONS:
            category = MAIN_MENU_OPTIONS[selected_option]
            if category["show_submenu"]:
                _run_submenu(mosip_authenticator, category["label"], category["use_ekyc"], category["use_kyc_auth"], category["use_key_binding"], category.get("kyc_v2", False))
            else:
                print(f"\n{category['label']} is not implemented yet.")
        elif selected_option == "0":
            deleted = mosip_authenticator.clear_bound_keys()
            if deleted:
                print(f"Deleted {deleted} bound key file(s) from new-keys/bound/.")
            print("Exiting...")
            break
        else:
            print("Invalid option. Please choose from the menu.")
