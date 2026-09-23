import json
from pathlib import Path

from authenticator import MOSIPAuthenticator


BASE_DIR = Path(__file__).resolve().parents[2]

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


MAIN_MENU_OPTIONS = {
    "1": {"label": "Auth", "use_ekyc": False, "show_submenu": True},
    "2": {"label": "eKYC", "use_ekyc": True, "show_submenu": True},
    "3": {"label": "kyc-auth (v1)", "use_ekyc": False, "show_submenu": False},
    "4": {"label": "kyc-exchange (v1)", "use_ekyc": False, "show_submenu": False},
    "5": {"label": "kyc-auth (v2)", "use_ekyc": False, "show_submenu": False},
    "6": {"label": "kyc-exchange (v2)", "use_ekyc": False, "show_submenu": False},
}


def _print_main_menu() -> None:
    print("\nMain Menu:")
    print("1. Auth (True/False)")
    print("2. eKYC")
    print("3. kyc-auth (v1)")
    print("4. kyc-exchange (v1)")
    print("5. kyc-auth (v2)")
    print("6. kyc-exchange (v2)")
    print("0. Exit")


def _print_submenu(category_label: str) -> None:
    print(f"\n{category_label} - Choose an option:")
    print("1. Demo auth")
    print("2. Send OTP request")
    print("3. OTP auth")
    print("4. Demo + OTP")
    print("5. Biometric auth")
    print("0. Back to main menu")


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


def _run_demo_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False) -> None:
    sample_file = _select_sample_file(BASE_DIR / "samples")
    if not sample_file:
        return

    try:
        with sample_file.open("r", encoding="utf-8") as file:
            json_data = json.load(file)
        auth_resp = mosip_authenticator.do_auth(json_data, use_ekyc=use_ekyc)
        if use_ekyc:
            _print_kyc_response(auth_resp)
        else:
            _print_readable_response(auth_resp)
    except Exception as exc:
        print(f"Failed to run demo auth: {exc}")


def _run_send_otp(mosip_authenticator: MOSIPAuthenticator) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    try:
        otp_resp = mosip_authenticator.send_otp_request(idvid)
        _print_readable_response(otp_resp)
    except Exception as exc:
        print(f"Failed to send OTP request: {exc}")


def _run_otp_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False) -> None:
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
        otp_auth_resp = mosip_authenticator.do_otp_auth(idvid, otp, transaction_id, use_ekyc=use_ekyc)
        if use_ekyc:
            _print_kyc_response(otp_auth_resp)
        else:
            _print_readable_response(otp_auth_resp)
    except Exception as exc:
        print(f"Failed to run OTP auth: {exc}")


def _run_bio_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False) -> None:
    idvid = input("Enter UIN/VID: ").strip()
    if not idvid:
        print("UIN/VID cannot be empty.")
        return

    selected_biometric = _select_biometric()
    if not selected_biometric:
        return

    label, bio_type, bio_sub_type = selected_biometric

    try:
        bio_auth_resp = mosip_authenticator.do_bio_auth(idvid, bio_type, bio_sub_type, use_ekyc=use_ekyc)
        print(f"\nSelected biometric: {label}")
        if use_ekyc:
            _print_kyc_response(bio_auth_resp)
        else:
            _print_readable_response(bio_auth_resp)
    except Exception as exc:
        print(f"Failed to run biometric auth: {exc}")


def _run_demo_otp_auth(mosip_authenticator: MOSIPAuthenticator, use_ekyc: bool = False) -> None:
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
        demo_otp_resp = mosip_authenticator.do_demo_otp_auth(json_data, idvid, otp, transaction_id, use_ekyc=use_ekyc)
        if use_ekyc:
            _print_kyc_response(demo_otp_resp)
        else:
            _print_readable_response(demo_otp_resp)
    except Exception as exc:
        print(f"Failed to run Demo + OTP auth: {exc}")


def _run_submenu(mosip_authenticator: MOSIPAuthenticator, category_label: str, use_ekyc: bool = False) -> None:
    while True:
        _print_submenu(category_label)
        selected_option = input("Enter option: ").strip()

        if selected_option == "1":
            _run_demo_auth(mosip_authenticator, use_ekyc)
        elif selected_option == "2":
            _run_send_otp(mosip_authenticator)
        elif selected_option == "3":
            _run_otp_auth(mosip_authenticator, use_ekyc)
        elif selected_option == "4":
            _run_demo_otp_auth(mosip_authenticator, use_ekyc)
        elif selected_option == "5":
            _run_bio_auth(mosip_authenticator, use_ekyc)
        elif selected_option == "0":
            return
        else:
            print("Invalid option. Please choose from the menu.")


def run_cli(mosip_authenticator: MOSIPAuthenticator) -> None:
    while True:
        _print_main_menu()
        selected_option = input("Enter option: ").strip()

        if selected_option in MAIN_MENU_OPTIONS:
            category = MAIN_MENU_OPTIONS[selected_option]
            if category["show_submenu"]:
                _run_submenu(mosip_authenticator, category["label"], category["use_ekyc"])
            else:
                print(f"\n{category['label']} is not implemented yet.")
        elif selected_option == "0":
            print("Exiting...")
            break
        else:
            print("Invalid option. Please choose from the menu.")
