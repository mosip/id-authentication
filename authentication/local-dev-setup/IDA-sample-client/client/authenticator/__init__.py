from .authenticator import MOSIPAuthenticator


def initialize(config, logger):
    authenticator = MOSIPAuthenticator(config,logger)
    return authenticator