import getpass
import json
from pathlib import Path

from authenticator import MOSIPAuthenticator


BASE_DIR = Path(__file__).resolve().parents[2]

DEFAULT_CONSENT_CLAIMS = "name,address,gender,birthdate,email,phone"
DEFAULT_LOCALES = "eng"
DEFAULT_RESP_TYPE = "JWT"
DEFAULT_VC_FORMAT = "ldp_vc"

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


def _print_kyc_auth_response(raw_response: str) -> None:
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
    "4": {"label": "kyc-auth & kyc-exchange (v2)", "use_ekyc": False, "use_kyc_auth": False, "use_key_binding": False, "show_submenu": False},
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


def _get_submenu_actions(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool, use_kyc_auth: bool, use_key_binding: bool) -> list:
    """Returns the (label, action) list of a submenu. Demo based options are not offered for kyc-auth; password and key binded token are kyc-auth only."""
    demo_auth = ("Demo auth", lambda: _run_demo_auth(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding))
    send_otp = ("Send OTP request", lambda: _run_send_otp(mosip_authenticator, use_kyc_auth, use_key_binding))
    otp_auth = ("OTP auth", lambda: _run_otp_auth(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding))
    demo_otp_auth = ("Demo + OTP", lambda: _run_demo_otp_auth(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding))
    bio_auth = ("Biometric auth", lambda: _run_bio_auth(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding))

    if use_kyc_auth:
        return [send_otp, otp_auth, bio_auth,
                ("Password", lambda: _run_password_auth(mosip_authenticator)),
                ("Key binded token", lambda: _run_key_binded_token_auth(mosip_authenticator)),
                ("kyc-exchange", lambda: _run_kyc_exchange(mosip_authenticator)),
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


def _print_auth_result(mosip_authenticator: MOSIPAuthenticator, raw_response: str, use_ekyc: bool, use_kyc_auth: bool, use_key_binding: bool = False) -> None:
    if use_key_binding:
        _print_key_binding_response(mosip_authenticator, raw_response)
    elif use_kyc_auth:
        _print_kyc_auth_response(raw_response)
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


def _run_otp_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False) -> None:
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
        otp_auth_resp = mosip_authenticator.do_otp_auth(idvid, otp, transaction_id, use_ekyc=use_ekyc, use_kyc_auth=use_kyc_auth, use_key_binding=use_key_binding)
        _print_auth_result(mosip_authenticator, otp_auth_resp, use_ekyc, use_kyc_auth, use_key_binding)
    except Exception as exc:
        print(f"Failed to run OTP auth: {exc}")


def _run_bio_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    selected_biometric = _select_biometric()
    if not selected_biometric:
        return

    label, bio_type, bio_sub_type = selected_biometric

    try:
        bio_auth_resp = mosip_authenticator.do_bio_auth(idvid, bio_type, bio_sub_type, use_ekyc=use_ekyc, use_kyc_auth=use_kyc_auth, use_key_binding=use_key_binding)
        print(f"\nSelected biometric: {label}")
        _print_auth_result(mosip_authenticator, bio_auth_resp, use_ekyc, use_kyc_auth, use_key_binding)
    except Exception as exc:
        print(f"Failed to run biometric auth: {exc}")


def _run_password_auth(mosip_authenticator: MOSIPAuthenticator) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    password = getpass.getpass("Enter password (input hidden): ")
    if not password:
        print("Password cannot be empty.")
        return

    try:
        resp = mosip_authenticator.do_password_auth(idvid, password)
        _print_kyc_auth_response(resp)
    except Exception as exc:
        print(f"Failed to run password auth: {exc}")


def _run_key_binded_token_auth(mosip_authenticator: MOSIPAuthenticator) -> None:
    idvid = input("Enter UIN/VID (must have a key bound via Identity key binding): ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    try:
        resp = mosip_authenticator.do_key_binded_token_auth(idvid)
        _print_kyc_auth_response(resp)
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
    print(f"response.encryptedKyc: {response_data.get('encryptedKyc')}")
    _print_errors(parsed_response)


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


def _run_submenu(mosip_authenticator: MOSIPAuthenticator, category_label: str, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False) -> None:
    if use_key_binding:
        _print_key_binding_warning(mosip_authenticator)
    actions = _get_submenu_actions(mosip_authenticator, use_ekyc, use_kyc_auth, use_key_binding)
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
                _run_submenu(mosip_authenticator, category["label"], category["use_ekyc"], category["use_kyc_auth"], category["use_key_binding"])
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
