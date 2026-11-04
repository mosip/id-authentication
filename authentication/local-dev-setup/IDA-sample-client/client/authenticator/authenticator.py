import string
import secrets
import logging
import sys
import traceback
import json
import base64
from pathlib import Path

from datetime import datetime
from datetime import timezone
from datetime import timedelta

from cryptography import x509
from cryptography.hazmat.primitives import hashes, asymmetric, ciphers, serialization
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives.serialization import pkcs12, BestAvailableEncryption
from jwcrypto import jwk, jws

from model import MOSIPAuthRequest, DemographicsModel, MOSIPEncryptAuthRequest, MOSIPEncryptAuthRequestKeyBind, KeyBindedTokenModel, CredentialDefRequest
from utils import CryptoUtility
from utils import RestUtility
from utils import CaptureUtility
from utils import KeyBindingUtility
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
        self.ida_kyc_request_id = config_obj.mosip_auth.ida_kyc_request_id
        self.ida_kyc_auth_request_id = config_obj.mosip_auth.ida_kyc_auth_request_id
        self.ida_kyc_exchange_request_id = config_obj.mosip_auth.ida_kyc_exchange_request_id
        self.ida_vci_exchange_request_id = config_obj.mosip_auth.ida_vci_exchange_request_id
        self.ida_key_binding_request_id = config_obj.mosip_auth.ida_key_binding_request_id
        self.key_binding_auth_factor_type = config_obj.mosip_auth.ida_key_binding_auth_factor_type
        self.idp_misp_lk = str(config_obj.mosip_auth.idp_misp_lk)
        self.auth_partner_id = str(config_obj.mosip_auth.auth_partner_id)
        self.oidc_client_id = str(config_obj.mosip_auth.oidc_client_id)
        self.ida_auth_env = config_obj.mosip_auth.ida_auth_env
        self.timestamp_format = config_obj.mosip_auth.timestamp_format
        self.authorization_header_constant = config_obj.mosip_auth.authorization_header_constant
        self.ida_auth_url = config_obj.mosip_auth_server.ida_auth_url
        self.ida_otp_url = config_obj.mosip_auth_server.ida_otp_url
        self.ida_ekyc_url = config_obj.mosip_auth_server.ida_ekyc_url
        self.ida_kyc_auth_url = config_obj.mosip_auth_server.ida_kyc_auth_url
        self.ida_kyc_exchange_url = config_obj.mosip_auth_server.ida_kyc_exchange_url
        self.ida_vci_exchange_url = config_obj.mosip_auth_server.ida_vci_exchange_url
        self.ida_key_binding_url = config_obj.mosip_auth_server.ida_key_binding_url
        self.key_binding_util = KeyBindingUtility(
            Path(__file__).resolve().parents[2] / config_obj.key_binding.bound_keys_dir,
            config_obj.key_binding.bound_p12_password,
            self.logger)
        self._binding_key = None
        self.last_bound_p12_path = None

        self.capture_util = CaptureUtility(
            config_obj.MockMDS,
            env=self.ida_auth_env,
            domain_uri=self.auth_domain_scheme,
            logger=self.logger,
        )

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
    
    def _resolve_target(self, use_ekyc: bool, use_kyc_auth: bool, use_key_binding: bool = False):
        """Returns (request_id, target_url, path_params) for auth / eKYC / kyc-auth (v1) / key binding requests."""
        if use_key_binding:
            return (self.ida_key_binding_request_id, self.ida_key_binding_url, self._partner_then_oidc_path())
        if use_kyc_auth:
            return (self.ida_kyc_auth_request_id, self.ida_kyc_auth_url,
                    self._partner_then_oidc_path())
        path_params = self.partner_misp_lk + '/' + self.partner_id + '/' + self.partner_apikey
        if use_ekyc:
            return self.ida_kyc_request_id, self.ida_ekyc_url, path_params
        return self.ida_auth_request_id, self.ida_auth_url, path_params

    def _partner_then_oidc_path(self):
        return self.idp_misp_lk + '/' + self.auth_partner_id + '/' + self.oidc_client_id

    def _set_key_binding(self, use_key_binding: bool, idvid: str):
        """Fills identityKeyBinding for the shared auth request (and clears it for non key-binding flows)."""
        self._binding_key = None
        self.last_bound_p12_path = None
        if not use_key_binding:
            self.auth_request.identityKeyBinding = {}
            return
        self._binding_key, reused = self.key_binding_util.load_or_generate_key(idvid)
        print('Using existing key pair for this UIN/VID.' if reused else 'Generated a new RSA key pair for this UIN/VID.')
        self.auth_request.identityKeyBinding = {
            'publicKeyJWK': self.key_binding_util.public_key_jwk(self._binding_key),
            'authFactorType': self.key_binding_auth_factor_type,
        }

    def _store_bound_certificate(self, idvid: str, response_text: str):
        """Imports the identity certificate from a key binding response into the p12 of this UIN/VID."""
        try:
            certificate = ((json.loads(response_text).get('response') or {}).get('identityCertificate'))
        except Exception:
            certificate = None
        if not certificate:
            print('No identityCertificate in the response. Key was not stored.')
            return
        self.last_bound_p12_path = self.key_binding_util.save(idvid, self._binding_key, certificate)

    def clear_bound_keys(self) -> int:
        return self.key_binding_util.clear_all()

    def do_auth(self, auth_req_data : dict, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False):
        vid = auth_req_data.pop('vid')
        
        print('\n\nReceived Auth Request.')
        try:
            demographic_data = DemographicsModel(**auth_req_data)
            timestamp = datetime.utcnow()
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            request_id, target_url, path_params = self._resolve_target(use_ekyc, use_kyc_auth, use_key_binding)
            self.auth_request.id = request_id
            self.auth_request.requestTime = timestamp_str
            self.auth_request.transactionID = ''.join([secrets.choice(string.digits) for _ in range(10)])
            self.auth_request.individualId = vid
            self._set_key_binding(use_key_binding, vid)

            self.auth_request.requestedAuth.demo = True
            self.auth_request.requestedAuth.otp = False
            self.auth_request.requestedAuth.pin = False
            self.auth_request.requestedAuth.bio = False
    
            request = MOSIPEncryptAuthRequest(
                timestamp = timestamp_str,
                demographics = demographic_data
            )
            
            self.auth_request.request, self.auth_request.requestSessionKey, self.auth_request.requestHMAC = \
                    self.crypto_util.encrypt_auth_data(request.json())

            full_request_json = self.auth_request.json()

            signature_header = {'Signature': self.crypto_util.sign_auth_request_data(full_request_json)}

            response = self.auth_rest_util.post_request(server_url=target_url, path_params=path_params, data=full_request_json, additional_headers=signature_header)
            print('Auth Request Completed Successfully.')

            if use_key_binding:
                self._store_bound_certificate(vid, response.text)
            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)
    # WIP    
    def send_otp_request(self, idvid: str, use_kyc_auth: bool = False, use_key_binding: bool = False):
        print('\n\nReceived OTP Request.')
        try:

            timestamp = datetime.now(timezone.utc)
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            request_dict = {}
            request_dict['id'] = self.ida_otp_request_id
            request_dict['version'] = '1.0'
            request_dict['individualId'] = idvid
            #request_dict['individualIdType'] = 'UIN'
            request_dict['transactionID'] = ''.join([secrets.choice(string.digits) for _ in range(10)])
            request_dict['requestTime'] = timestamp_str
            request_dict['otpChannel'] = ['EMAIL', 'PHONE']
            
            request_json = json.dumps(request_dict)

            signature_header = {'Signature': self.crypto_util.sign_auth_request_data(request_json)}

            if use_kyc_auth or use_key_binding:
                path_params = self._partner_then_oidc_path()
            else:
                path_params = self.partner_misp_lk + '/' + self.partner_id + '/' + self.partner_apikey
            response = self.auth_rest_util.post_request(server_url=self.ida_otp_url, path_params=path_params, data=request_json, additional_headers=signature_header)
            print('Auth Request Processed Completed.')
            
            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)

    def do_otp_auth(self, idvid: str, otp: str, transaction_id: str, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False):
        print('\n\nReceived OTP Auth Request.')
        try:
            timestamp = datetime.utcnow()
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            request_id, target_url, path_params = self._resolve_target(use_ekyc, use_kyc_auth, use_key_binding)
            self.auth_request.id = request_id
            self.auth_request.requestTime = timestamp_str
            self.auth_request.transactionID = transaction_id
            self.auth_request.individualId = idvid
            self._set_key_binding(use_key_binding, idvid)

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

            response = self.auth_rest_util.post_request(
                server_url=target_url,
                path_params=path_params,
                data=full_request_json,
                additional_headers=signature_header
            )
            print('OTP Auth Request Completed Successfully.')

            if use_key_binding:
                self._store_bound_certificate(idvid, response.text)
            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing OTP Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)

    def do_demo_otp_auth(self, auth_req_data: dict, idvid: str, otp: str, transaction_id: str, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False):
        print('\n\nReceived Demo + OTP Auth Request.')
        try:
            req_data = dict(auth_req_data)
            req_data.pop('vid', None)
            demographic_data = DemographicsModel(**req_data)

            timestamp = datetime.utcnow()
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            request_id, target_url, path_params = self._resolve_target(use_ekyc, use_kyc_auth, use_key_binding)
            self.auth_request.id = request_id
            self.auth_request.requestTime = timestamp_str
            self.auth_request.transactionID = transaction_id
            self.auth_request.individualId = idvid
            self._set_key_binding(use_key_binding, idvid)

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

            response = self.auth_rest_util.post_request(
                server_url=target_url,
                path_params=path_params,
                data=full_request_json,
                additional_headers=signature_header
            )
            print('Demo + OTP Auth Request Completed Successfully.')

            if use_key_binding:
                self._store_bound_certificate(idvid, response.text)
            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing Demo + OTP Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)

    def do_bio_auth(self, idvid: str, bio_type: str, bio_sub_type: str, use_ekyc: bool = False, use_kyc_auth: bool = False, use_key_binding: bool = False):
        print('\n\nReceived Biometric Auth Request.')
        try:
            timestamp = datetime.utcnow()
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'
            transaction_id = ''.join([secrets.choice(string.digits) for _ in range(10)])

            print('Capturing biometric from Mock MDS...')
            parsed_biometrics = self.capture_util.capture_biometric(
                transaction_id=transaction_id,
                capture_time=timestamp_str,
                bio_type=bio_type,
                bio_sub_type=bio_sub_type,
            )
            print('Biometric capture completed successfully.')

            request_id, target_url, path_params = self._resolve_target(use_ekyc, use_kyc_auth, use_key_binding)
            self.auth_request.id = request_id
            self.auth_request.requestTime = timestamp_str
            self.auth_request.transactionID = transaction_id
            self.auth_request.individualId = idvid
            self._set_key_binding(use_key_binding, idvid)

            self.auth_request.requestedAuth.demo = False
            self.auth_request.requestedAuth.otp = False
            self.auth_request.requestedAuth.pin = False
            self.auth_request.requestedAuth.bio = True

            bio_request = {
                "timestamp": timestamp_str,
                "biometrics": parsed_biometrics,
            }

            self.auth_request.request, self.auth_request.requestSessionKey, self.auth_request.requestHMAC = \
                self.crypto_util.encrypt_auth_data(json.dumps(bio_request))

            full_request_json = self.auth_request.json()
            signature_header = {'Signature': self.crypto_util.sign_auth_request_data(full_request_json)}

            response = self.auth_rest_util.post_request(
                server_url=target_url,
                path_params=path_params,
                data=full_request_json,
                additional_headers=signature_header
            )
            print('Biometric Auth Request Completed Successfully.')

            if use_key_binding:
                self._store_bound_certificate(idvid, response.text)
            return response.text
        except AuthenticatorException:
            raise
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing Biometric Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)

    def send_kyc_exchange_request(self, idvid: str, kyc_token: str, consent_obtained: list, locales: list, transaction_id: str = None, resp_type: str = 'JWT'):
        print('\n\nReceived KYC Exchange Request.')
        try:
            timestamp = datetime.now(timezone.utc)
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            request_dict = {}
            request_dict['id'] = self.ida_kyc_exchange_request_id
            request_dict['version'] = '1.0'
            request_dict['requestTime'] = timestamp_str
            request_dict['transactionID'] = transaction_id or ''.join([secrets.choice(string.digits) for _ in range(10)])
            request_dict['individualId'] = idvid
            request_dict['kycToken'] = kyc_token
            request_dict['consentObtained'] = consent_obtained
            request_dict['locales'] = locales
            request_dict['respType'] = resp_type

            request_json = json.dumps(request_dict)
            signature_header = {'Signature': self.crypto_util.sign_auth_request_data(request_json)}

            path_params = self._partner_then_oidc_path()
            response = self.auth_rest_util.post_request(
                server_url=self.ida_kyc_exchange_url,
                path_params=path_params,
                data=request_json,
                additional_headers=signature_header
            )
            print('KYC Exchange Request Completed Successfully.')

            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing KYC Exchange Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)


    def _send_kyc_auth(self, idvid: str, transaction_id: str, timestamp_str: str, request_dict: dict, log_name: str):
        """Sends a kyc-auth (v1) request whose encrypted `request` is request_dict."""
        self.auth_request.id = self.ida_kyc_auth_request_id
        self.auth_request.requestTime = timestamp_str
        self.auth_request.transactionID = transaction_id
        self.auth_request.individualId = idvid
        self._set_key_binding(False, idvid)

        self.auth_request.requestedAuth.demo = False
        self.auth_request.requestedAuth.otp = False
        self.auth_request.requestedAuth.pin = False
        self.auth_request.requestedAuth.bio = False

        self.auth_request.request, self.auth_request.requestSessionKey, self.auth_request.requestHMAC = \
            self.crypto_util.encrypt_auth_data(json.dumps(request_dict))

        full_request_json = self.auth_request.json()
        signature_header = {'Signature': self.crypto_util.sign_auth_request_data(full_request_json)}

        response = self.auth_rest_util.post_request(
            server_url=self.ida_kyc_auth_url,
            path_params=self._partner_then_oidc_path(),
            data=full_request_json,
            additional_headers=signature_header
        )
        print('{} Request Completed Successfully.'.format(log_name))
        return response.text

    def do_password_auth(self, idvid: str, password: str):
        print('\n\nReceived Password Auth Request.')
        try:
            timestamp = datetime.utcnow()
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'
            transaction_id = ''.join([secrets.choice(string.digits) for _ in range(10)])
            print('Transaction Id: {}'.format(transaction_id))

            return self._send_kyc_auth(idvid, transaction_id, timestamp_str,
                                       {"timestamp": timestamp_str, "password": password}, 'Password Auth')
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing Password Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)

    def _build_key_binded_token(self, idvid: str, private_key, certificate) -> str:
        """Signs a WLA token (JWT, RS256) with the key bound to this UIN/VID."""
        now = datetime.now(timezone.utc)
        token_data = {
            'sub': idvid,
            'aud': 'ida-binding',
            'iss': 'Python-Client',
            'iat': int(now.timestamp()),
            'exp': int((now + timedelta(minutes=60)).timestamp()),
        }
        signing_key = jwk.JWK.from_pem(private_key.private_bytes(
            serialization.Encoding.PEM, serialization.PrivateFormat.PKCS8, serialization.NoEncryption()))
        jws_object = jws.JWS(json.dumps(token_data))
        jws_object.add_signature(
            signing_key,
            None,
            json.dumps({'alg': 'RS256',
                        'x5t#S256': base64.urlsafe_b64encode(certificate.fingerprint(hashes.SHA256())).decode('UTF-8').rstrip('=')}))
        return jws_object.serialize(compact=True)

    def do_key_binded_token_auth(self, idvid: str):
        print('\n\nReceived Key Binded Token Auth Request.')
        try:
            bound = self.key_binding_util.load_key_and_certificate(idvid)
            if not bound:
                print('No bound key found for this UIN/VID. Run Identity key binding (main menu option 5) first.')
                raise AuthenticatorException(Errors.AUT_BAS_001.name, 'No bound key found for UIN/VID: {}'.format(idvid))
            private_key, certificate = bound

            timestamp = datetime.utcnow()
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'
            transaction_id = ''.join([secrets.choice(string.digits) for _ in range(10)])
            print('Transaction Id: {}'.format(transaction_id))

            key_binded_token = {
                'type': self.key_binding_auth_factor_type,
                'token': self._build_key_binded_token(idvid, private_key, certificate),
                'format': 'jwt',
            }
            return self._send_kyc_auth(idvid, transaction_id, timestamp_str,
                                       {"timestamp": timestamp_str, "keyBindedTokens": [key_binded_token]}, 'Key Binded Token Auth')
        except AuthenticatorException:
            raise
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing Key Binded Token Auth Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)


    def generate_did_jwk(self) -> str:
        """did:jwk of a freshly generated, throw-away RSA 2048 key (the private key is discarded)."""
        jwk_json = json.dumps(self.key_binding_util.public_key_jwk(rsa.generate_private_key(public_exponent=65537, key_size=2048)),
                              separators=(',', ':'))
        return 'did:jwk:' + base64.urlsafe_b64encode(jwk_json.encode('utf-8')).decode('utf-8').rstrip('=')

    def send_vci_exchange_request(self, idvid: str, vc_auth_token: str, vc_format: str,
                                  locales: list, transaction_id: str):
        print('\n\nReceived VCI Exchange Request.')
        try:
            timestamp = datetime.now(timezone.utc)
            timestamp_str = timestamp.strftime(self.timestamp_format) + timestamp.strftime('.%f')[0:4] + 'Z'

            request_dict = {}
            request_dict['id'] = self.ida_vci_exchange_request_id
            request_dict['version'] = '1.0'
            request_dict['requestTime'] = timestamp_str
            request_dict['transactionID'] = transaction_id
            request_dict['individualId'] = idvid
            request_dict['vcAuthToken'] = vc_auth_token
            request_dict['credSubjectId'] = self.generate_did_jwk()
            print('credSubjectId: {}'.format(request_dict['credSubjectId']))
            request_dict['vcFormat'] = vc_format
            request_dict['locales'] = locales
            request_dict['credentialsDefinition'] = {
                'context': [],
                'type': ['MOSIPVerifiableCredential', 'VerifiableCredential'],
                'credentialSubject': {},
            }

            request_json = json.dumps(request_dict)
            signature_header = {'Signature': self.crypto_util.sign_auth_request_data(request_json)}

            response = self.auth_rest_util.post_request(
                server_url=self.ida_vci_exchange_url,
                path_params=self._partner_then_oidc_path(),
                data=request_json,
                additional_headers=signature_header
            )
            print('VCI Exchange Request Completed Successfully.')

            return response.text
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Processing VCI Exchange Request. Error Message: {}'.format(exp))
            raise AuthenticatorException(Errors.AUT_BAS_001.name, Errors.AUT_BAS_001.value)
