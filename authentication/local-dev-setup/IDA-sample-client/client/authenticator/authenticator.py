import string
import secrets
import logging
import sys
import traceback
import json

from datetime import datetime
from datetime import timezone

from cryptography import x509
from cryptography.hazmat.primitives import hashes, asymmetric, ciphers, serialization
from cryptography.hazmat.primitives.serialization import pkcs12, BestAvailableEncryption
from jwcrypto import jwk, jws

from model import MOSIPAuthRequest, DemographicsModel, MOSIPEncryptAuthRequest, MOSIPEncryptAuthRequestKeyBind, KeyBindedTokenModel, CredentialDefRequest
from utils import CryptoUtility
from utils import RestUtility
from exceptions import AuthenticatorException, AuthenticatorCryptoException, Errors

class MOSIPAuthenticator:

    def __init__(self, config_obj, logger=None, **kwargs ):
        if not logger:
            self.logger = self._init_logger(config_obj.logging.log_file_path)
        else:
            self.logger = logger

        self.auth_rest_util = RestUtility(config_obj.mosip_auth.authorization_header_constant)
        self.crypto_util = CryptoUtility(config_obj.crypto_encrypt, config_obj.crypto_signature)

        self.auth_domain_scheme = config_obj.mosip_auth_server.ida_auth_domain_uri
       
        self.partner_misp_lk =  str(config_obj.mosip_auth.partner_misp_lk)
        self.partner_id = str(config_obj.mosip_auth.partner_id)
        self.partner_apikey = str(config_obj.mosip_auth.partner_apikey)

        self.ida_auth_version = config_obj.mosip_auth.ida_auth_version
        self.ida_auth_request_id = config_obj.mosip_auth.ida_auth_request_id
        self.ida_otp_request_id = config_obj.mosip_auth.ida_otp_request_id
        self.ida_auth_env = config_obj.mosip_auth.ida_auth_env
        self.timestamp_format = config_obj.mosip_auth.timestamp_format
        self.authorization_header_constant = config_obj.mosip_auth.authorization_header_constant
        self.ida_auth_url = config_obj.mosip_auth_server.ida_auth_url
        self.ida_otp_url = config_obj.mosip_auth_server.ida_otp_url

        self.auth_request = MOSIPAuthRequest(
            id = self.ida_auth_request_id,
            version = self.ida_auth_version,
            env = self.ida_auth_env,
            domainUri = self.auth_domain_scheme,
            specVersion = self.ida_auth_version,
            consentObtained = True,
            metadata = {},
            thumbprint = self.crypto_util.enc_cert_thumbprint,
            individualId = '',
            transactionID = '',
            requestTime = '',
            request = '',
            requestSessionKey = '',
            requestHMAC = '',
            identityKeyBinding = {},
            claimsMetadataRequired = True
        )

    @staticmethod
    def _init_logger(filename):
        logger = logging.getLogger()
        logger.setLevel(logging.INFO)
        fileHandler = logging.FileHandler(filename)
        streamHandler = logging.StreamHandler(sys.stdout)
        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        streamHandler.setFormatter(formatter)
        fileHandler.setFormatter(formatter)
        logger.addHandler(streamHandler)
        logger.addHandler(fileHandler)
        return logger
    
    def do_auth(self, auth_req_data : dict):
        vid = auth_req_data.pop('vid')
        
        print('\n\nReceived Auth Request.')
        try:
            demographic_data = DemographicsModel(**auth_req_data)
            timestamp = datetime.utcnow()
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            self.auth_request.requestTime = timestamp_str
            self.auth_request.transactionID = ''.join([secrets.choice(string.digits) for _ in range(10)])
            self.auth_request.individualId = vid

            self.auth_request.requestedAuth.demo = True
    
            request = MOSIPEncryptAuthRequest(
                timestamp = timestamp_str,
                demographics = demographic_data
            )
            
            self.auth_request.request, self.auth_request.requestSessionKey, self.auth_request.requestHMAC = \
                    self.crypto_util.encrypt_auth_data(request.json())

            full_request_json = self.auth_request.json()

            signature_header = {'Signature': self.crypto_util.sign_auth_request_data(full_request_json)}

            path_params = self.partner_misp_lk + '/' + self.partner_id + '/' + self.partner_apikey
            response = self.auth_rest_util.post_request(server_url=self.ida_auth_url, path_params=path_params, data=full_request_json, additional_headers=signature_header)
            print('Auth Request Completed Successfully.')
            
            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)
    # WIP    
    def send_otp_request(self, idvid: str):
        print('\n\nReceived OTP Request.')
        try:

            timestamp = datetime.now(timezone.utc)
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            request_dict = {}
            request_dict['id'] = self.ida_otp_request_id
            request_dict['version'] = '1.0'
            request_dict['individualId'] = idvid
            request_dict['individualIdType'] = 'UIN'
            request_dict['transactionID'] = ''.join([secrets.choice(string.digits) for _ in range(10)])
            request_dict['requestTime'] = timestamp_str
            request_dict['otpChannel'] = ['email', 'PHONE']
            
            request_json = json.dumps(request_dict)

            signature_header = {'Signature': self.crypto_util.sign_auth_request_data(request_json)}

            path_params = self.partner_misp_lk + '/' + self.partner_id + '/' + self.partner_apikey
            #path_params = self.partner_id + '/' + self.partner_apikey
            response = self.auth_rest_util.post_request(server_url=self.ida_otp_url, path_params=path_params, data=request_json, additional_headers=signature_header)
            print('Auth Request Processed Completed.')
            
            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)

    def do_otp_auth(self, idvid: str, otp: str, transaction_id: str):
        print('\n\nReceived OTP Auth Request.')
        try:
            timestamp = datetime.utcnow()
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            self.auth_request.requestTime = timestamp_str
            self.auth_request.transactionID = transaction_id
            self.auth_request.individualId = idvid

            self.auth_request.requestedAuth.demo = False
            self.auth_request.requestedAuth.otp = True
            self.auth_request.requestedAuth.pin = False
            self.auth_request.requestedAuth.bio = False

            otp_request = {
                "timestamp": timestamp_str,
                "otp": otp
            }

            self.auth_request.request, self.auth_request.requestSessionKey, self.auth_request.requestHMAC = \
                self.crypto_util.encrypt_auth_data(json.dumps(otp_request))

            full_request_json = self.auth_request.json()
            signature_header = {'Signature': self.crypto_util.sign_auth_request_data(full_request_json)}

            path_params = self.partner_misp_lk + '/' + self.partner_id + '/' + self.partner_apikey
            response = self.auth_rest_util.post_request(
                server_url=self.ida_auth_url,
                path_params=path_params,
                data=full_request_json,
                additional_headers=signature_header
            )
            print('OTP Auth Request Completed Successfully.')

            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing OTP Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)

    def do_demo_otp_auth(self, auth_req_data: dict, idvid: str, otp: str, transaction_id: str):
        print('\n\nReceived Demo + OTP Auth Request.')
        try:
            req_data = dict(auth_req_data)
            req_data.pop('vid', None)
            demographic_data = DemographicsModel(**req_data)

            timestamp = datetime.utcnow()
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            self.auth_request.requestTime = timestamp_str
            self.auth_request.transactionID = transaction_id
            self.auth_request.individualId = idvid

            self.auth_request.requestedAuth.demo = True
            self.auth_request.requestedAuth.otp = True
            self.auth_request.requestedAuth.pin = False
            self.auth_request.requestedAuth.bio = False

            demo_otp_request = {
                "timestamp": timestamp_str,
                "demographics": json.loads(demographic_data.json()),
                "otp": otp
            }

            self.auth_request.request, self.auth_request.requestSessionKey, self.auth_request.requestHMAC = \
                self.crypto_util.encrypt_auth_data(json.dumps(demo_otp_request))

            full_request_json = self.auth_request.json()
            signature_header = {'Signature': self.crypto_util.sign_auth_request_data(full_request_json)}

            path_params = self.partner_misp_lk + '/' + self.partner_id + '/' + self.partner_apikey
            response = self.auth_rest_util.post_request(
                server_url=self.ida_auth_url,
                path_params=path_params,
                data=full_request_json,
                additional_headers=signature_header
            )
            print('Demo + OTP Auth Request Completed Successfully.')

            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing Demo + OTP Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)