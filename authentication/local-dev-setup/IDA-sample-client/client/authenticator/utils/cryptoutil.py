import os
import base64
import json
import logging
import traceback

from cryptography import x509
from cryptography.hazmat.primitives import hashes, asymmetric, ciphers, serialization
from cryptography.hazmat.primitives.serialization import pkcs12, BestAvailableEncryption
from jwcrypto import jwk, jws

from exceptions import AuthenticatorCryptoException, Errors

class CryptoUtility:

    def __init__(self, encrypt_config, sign_config, logger=None, **kwargs):
        if not logger:
            self.logger = logging.getLogger(__name__)
        else:
            self.logger = logger

        self.encrypt_cert_obj = CryptoUtility._get_certificate_obj(encrypt_config.encrypt_cert_path, self.logger)
        self.sign_private_key, self.sign_cert, self.ca_chain = CryptoUtility._get_priv_key_cert(sign_config.sign_p12_file_path, 
                                                str(sign_config.sign_p12_file_password), self.logger)

        self.sign_priv_key_jws = CryptoUtility._get_jwk_private_key(self.sign_private_key, str(sign_config.sign_p12_file_password), self.logger)

        self.enc_cert_thumbprint = base64.urlsafe_b64encode(self.encrypt_cert_obj.fingerprint(hashes.SHA256()))

        # Initalizing OAEP padding algorithm.
        self.asymmetric_encrypt_padding = asymmetric.padding.OAEP(
                                                mgf=asymmetric.padding.MGF1(algorithm=hashes.SHA256()),
                                                algorithm=hashes.SHA256(),
                                                label=None)

        self.symmetric_key_size = encrypt_config.symmetric_key_size
        self.symmetric_nonce_size = int(encrypt_config.symmetric_nonce_size / 8)
        self.symmetric_gcm_tag_size = int(encrypt_config.symmetric_gcm_tag_size / 8)
        self.algorithm = sign_config.algorithm
        
    
    @staticmethod
    def _get_certificate_obj(cert_path, logger):
        # will add not 'None' validation in DynaConf validation API.
        logger.debug('Creating certificate Object for the file Path: {}'.format(cert_path))
        try:
            with open(cert_path, 'rb') as file:
                cert = x509.load_pem_x509_certificate(file.read())
                logger.debug('Cretificate Object Creation successful(PEM Format).')
                return cert
        except:
            exp = traceback.format_exc() 
            logger.error('Error Reading PEM file format. Error Message: {}'.format(exp))

        try:
            with open(cert_path, 'rb') as file:
                cert = x509.load_der_x509_certificate(file.read())
                logger.debug('Cretificate Object Creation successful(DER Format).')
                return cert
        except:
            exp = traceback.format_exc()
            logger.error('Error Reading both PEM/DER file format. Error: {}'.format(exp))
            raise AuthenticatorCryptoException(Errors.AUT_CRY_001.name, Errors.AUT_CRY_001.value.format(cert_path))

    @staticmethod
    def _get_priv_key_cert(p12_file_path, p12_file_pass, logger):
       
        logger.debug('Reading P12 file. File Path: {}'.format(p12_file_path))
        try: 
            with open(p12_file_path, 'rb') as file:
                pem_bytes = file.read()
                return pkcs12.load_key_and_certificates(pem_bytes, bytes(p12_file_pass, 'utf-8'))
        except:
            exp = traceback.format_exc()
            logger.error('Error Loading P12 file to create objects. Error: {}'.format(exp))
            raise AuthenticatorCryptoException(Errors.AUT_CRY_002.name, Errors.AUT_CRY_002.value.format(p12_file_path))    
    
    @staticmethod
    def _get_jwk_private_key(priv_key_obj, key_password, logger):

        key_pwd_bytes = bytes(key_password, 'utf-8')
        priv_key_pem = priv_key_obj.private_bytes(encoding=serialization.Encoding.PEM, format=serialization.PrivateFormat.PKCS8,
                            encryption_algorithm=BestAvailableEncryption(key_pwd_bytes))
        logger.debug('Creating JWK key for JWS signing.')
        return jwk.JWK.from_pem(priv_key_pem, password=key_pwd_bytes)
        
    def _asymmetric_encrypt(self, aes_random_key):
        self.logger.debug('Encrypting the AES Random Key.')
        public_key_obj = self.encrypt_cert_obj.public_key()
        return public_key_obj.encrypt(aes_random_key, self.asymmetric_encrypt_padding)
    
    def _symmetric_encrypt(self, data, key):
        self.logger.debug('Encrypting the Auth Data using AES Key.')
        iv = os.urandom(self.symmetric_nonce_size)
        aes_encryptor_obj = ciphers.Cipher(ciphers.algorithms.AES(key),
                                   ciphers.modes.GCM(iv, tag=None, min_tag_length=self.symmetric_gcm_tag_size)).encryptor()

        enc_data = aes_encryptor_obj.update(data) + aes_encryptor_obj.finalize()
        enc_data_final = enc_data + aes_encryptor_obj.tag + iv
        return enc_data_final
    
    def encrypt_auth_data(self, auth_data):
        self.logger.debug('Request for Auth Data Encryption.')
        if isinstance(auth_data, str):
            auth_data_bytes = auth_data.encode('UTF-8')
        elif isinstance(auth_data, bytes):
            auth_data_bytes = auth_data
        else:
            raise ValueError('Unrecognised type')
        
        try:
            # Generate a random AES Key and encrypt Auth Request Data using the generated random key.
            aes_key = os.urandom(int(self.symmetric_key_size/8))
            encrypted_auth_data = self._symmetric_encrypt(auth_data_bytes, aes_key)
            encrypted_auth_b64_data = base64.urlsafe_b64encode(encrypted_auth_data).decode('UTF-8')
            self.logger.debug('Generating AES Key and encrypting Auth Data Completed.')

            # Encrypt the random generated key using the IDA partner certificate received from IDA server.
            encrypted_aes_key = self._asymmetric_encrypt(aes_key)
            encrypted_aes_key_b64 = base64.urlsafe_b64encode(encrypted_aes_key).decode('UTF-8')
            self.logger.debug('Encrypting Random AES Key Completed.')

            # Generate SHA256 for the Auth Request Data 
            sha256_hash_obj = hashes.Hash(hashes.SHA256())
            sha256_hash_obj.update(auth_data_bytes)
            auth_data_hash = sha256_hash_obj.finalize().hex().upper().encode('UTF-8')
            enc_auth_data_hash = self._symmetric_encrypt(auth_data_hash, aes_key)
            enc_auth_data_hash_b64 = base64.urlsafe_b64encode(enc_auth_data_hash).decode('UTF-8')
            self.logger.debug('Generation of SHA256 Hash for the Auth Data completed.')

            return encrypted_auth_b64_data, encrypted_aes_key_b64, enc_auth_data_hash_b64
        except:
            exp = traceback.format_exc()
            self.logger.error('Error encrypting Auth Data. Error Message: {}'.format(exp))
            raise AuthenticatorCryptoException(Errors.AUT_CRY_003.name, Errors.AUT_CRY_003.value)
            
    
    def sign_auth_request_data(self, auth_request_data):
        self.logger.debug('Request for Sign Auth Request Data.')
        try:
            jws_object = jws.JWS(auth_request_data.encode('UTF-8'))
            jws_object.add_signature(
                self.sign_priv_key_jws,
                None,
                json.dumps({'alg': self.algorithm,
                            'x5c':[base64.b64encode(self.sign_cert.public_bytes(encoding=serialization.Encoding.PEM)).decode('UTF-8')]}), # Protected header attributes, is IDA checking for protected attributes?
                json.dumps({'kid': base64.b64encode(self.sign_cert.fingerprint(hashes.SHA256())).decode('UTF-8')}) # UnProtected Header attributes.
            )

            jws_signature = jws_object.serialize(compact=True).split('.')
            self.logger.debug('Generation for JWS Signature completed.')
            return jws_signature[0] + '..' + jws_signature[2]
        except:
            exp = traceback.format_exc()
            self.logger.error('Error Signing Auth Data. Error Message: {}'.format(exp))
            raise AuthenticatorCryptoException(Errors.AUT_CRY_004.name, Errors.AUT_CRY_004.value)