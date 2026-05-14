import logging
import os
import sys
from fastapi import FastAPI
from dynaconf import Dynaconf

def init_app(config):
    description = """
    MOSIP Token Seeder API is a toolkit for generating MOSIP token for the enrolled users

    ***********************************
    Further details goes here 
    ***********************************
    """
    app = FastAPI(
        title="MOSIP Token Seeder",
        version='0.1.0',
        description=description,
        contact={
            "url": "https://mosip.io/contact.php",
            "email": "info@mosip.io",
        },
        license_info={
            "name":"Mozilla Public License 2.0",
            "url": "https://www.mozilla.org/en-US/MPL/2.0/",
        },
        root_path=config.root.context_path if config.root.context_path else '/'
    )
    return app

def get_current_worker_id(config):
    if config.root.pid_grep_name=='local':
        return
    import subprocess
    try:
        pid_arr = sorted([int(a) for a in str(subprocess.check_output(['pgrep','-f',config.root.pid_grep_name]), 'UTF-8').split('\n') if a])
        config.gunicorn.worker_id = pid_arr.index(os.getpid()) - 1
    except:
        config.gunicorn.worker_id = -1

def get_pod_id(config):
    config.docker.pod_id = str(config.docker.pod_name.split('-')[-1])

def init_config():
    config = Dynaconf(
        settings_files=[
            os.path.join(os.path.dirname(__file__),'authenticator','authenticator-config.toml'),
            os.path.join(os.path.dirname(__file__),'config.toml'),
            "config.toml",
            "/app/token_seeder.conf",
        ],
        envvar_prefix="TOKENSEEDER",
        environments=False
    )
    get_current_worker_id(config)
    get_pod_id(config)
    return config

def init_logger(config):
    logger = logging.getLogger()
    logger.setLevel(getattr(logging,config.logging.loglevel))
    fileHandler = logging.FileHandler(config.logging.log_file_name)
    streamHandler = logging.StreamHandler(sys.stdout)
    formatter = logging.Formatter(config.logging.log_format)
    streamHandler.setFormatter(formatter)
    fileHandler.setFormatter(formatter)
    logger.addHandler(streamHandler)
    logger.addHandler(fileHandler)
    return logger
