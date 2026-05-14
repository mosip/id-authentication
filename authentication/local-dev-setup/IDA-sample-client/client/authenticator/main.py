from pathlib import Path

from dynaconf import Dynaconf

from authenticator import MOSIPAuthenticator
from cli_options import run_cli

CONFIG_FILES = [
    str(Path(__file__).resolve().parent / "authenticator-config-local.toml"),
    "/app/token_seeder.conf",
]
config = Dynaconf(settings_files=CONFIG_FILES, envvar_prefix="MOSIP", environments=False)

if __name__ == "__main__":
    print(f"Configured IDA URL: {config.mosip_auth_server.ida_auth_url}")
    mosip_authenticator = MOSIPAuthenticator(config)
    run_cli(mosip_authenticator)