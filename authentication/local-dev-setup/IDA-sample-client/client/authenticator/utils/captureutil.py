import base64
import json
import logging

import requests

from exceptions import AuthenticatorException, Errors


class CaptureUtility:

    def __init__(self, mock_mds_config, env: str, domain_uri: str, logger=None):
        self.logger = logger or logging.getLogger(__name__)
        self.capture_url = mock_mds_config.mock_mds_capture_url
        self.capture_method = mock_mds_config.mock_mds_capture_request_method
        self.spec_version = mock_mds_config.mock_mds_spec_version
        self.timeout = mock_mds_config.mock_mds_timeout
        self.requested_score = mock_mds_config.mock_mds_requested_score
        self.device_id_by_bio_type = {
            'face': mock_mds_config.mock_mds_face_device_id,
            'finger': mock_mds_config.mock_mds_finger_device_id,
            'iris': mock_mds_config.mock_mds_iris_device_id,
        }
        self.device_sub_id = mock_mds_config.mock_mds_device_sub_id
        self.env = env
        self.domain_uri = domain_uri

    def _resolve_device_id(self, bio_type: str) -> str:
        device_id = self.device_id_by_bio_type.get((bio_type or '').lower())
        if device_id is None:
            raise AuthenticatorException(
                Errors.AUT_BAS_001.name,
                f'No Mock MDS device id configured for biometric type: {bio_type}',
            )
        return device_id

    @staticmethod
    def decode_jwt_payload(jwt_token: str) -> dict:
        parts = jwt_token.split('.')
        if len(parts) != 3:
            raise ValueError('Invalid JWT format in capture response')

        payload = parts[1]
        padding = '=' * (-len(payload) % 4)
        decoded = base64.urlsafe_b64decode(payload + padding)
        return decoded.decode('utf-8')

    def build_capture_request(
        self,
        transaction_id: str,
        capture_time: str,
        bio_type: str,
        bio_sub_type: str,
    ) -> dict:
        bio_sub_types = [bio_sub_type] if bio_sub_type else ['']
        device_id = self._resolve_device_id(bio_type)

        return {
            'env': self.env,
            'purpose': 'Auth',
            'specVersion': self.spec_version,
            'timeout': self.timeout,
            'captureTime': capture_time,
            'transactionId': transaction_id,
            'domainUri': self.domain_uri,
            'bio': [
                {
                    'type': bio_type,
                    'count': '1',
                    'bioSubType': bio_sub_types,
                    'requestedScore': self.requested_score,
                    'deviceId': device_id,
                    'deviceSubId': self.device_sub_id,
                    'previousHash': '',
                }
            ],
        }

    def invoke_capture(self, payload: dict) -> dict:
        self.logger.info('Invoking Mock MDS capture at %s', self.capture_url)
        print(f'Capture request URL: {self.capture_url} (method: {self.capture_method})')

        response = requests.request(
            self.capture_method,
            self.capture_url,
            json=payload,
            headers={'Content-Type': 'application/json'},
        )

        if not response.ok:
            raise AuthenticatorException(
                Errors.AUT_BAS_001.name,
                f'Mock MDS capture failed with status {response.status_code}: {response.text}',
            )

        return response.json()

    def parse_capture_response(self, response_json: dict) -> list:
        biometrics = response_json.get('biometrics')
        if not biometrics:
            raise AuthenticatorException(
                Errors.AUT_BAS_001.name,
                'Mock MDS capture response missing biometrics array',
            )

        parsed_biometrics = []
        for item in biometrics:
            error = item.get('error')
            if error and str(error.get('errorCode', '')) not in ('', '0'):
                error_code = error.get('errorCode', 'unknown')
                error_info = error.get('errorInfo', 'Capture error')
                raise AuthenticatorException(
                    Errors.AUT_BAS_001.name,
                    f'Mock MDS capture error {error_code}: {error_info}',
                )

            data = item.get('data')
            '''if isinstance(data, str):
                data = self.decode_jwt_payload(data)
            elif not isinstance(data, dict):
                raise AuthenticatorException(
                    Errors.AUT_BAS_001.name,
                    'Mock MDS capture response has invalid data field',
                )
            '''
            parsed_biometrics.append({
                'specVersion': item.get('specVersion', self.spec_version),
                'data': data,
                'hash': item.get('hash', ''),
                'sessionKey': item.get('sessionKey', ''),
                'thumbprint': item.get('thumbprint', ''),
            })

        return parsed_biometrics

    def capture_biometric(
        self,
        transaction_id: str,
        capture_time: str,
        bio_type: str,
        bio_sub_type: str,
    ) -> list:
        payload = self.build_capture_request(
            transaction_id=transaction_id,
            capture_time=capture_time,
            bio_type=bio_type,
            bio_sub_type=bio_sub_type,
        )
        response_json = self.invoke_capture(payload)
        return self.parse_capture_response(response_json)
