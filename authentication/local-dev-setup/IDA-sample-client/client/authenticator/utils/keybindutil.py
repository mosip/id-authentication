import base64
import hashlib
import logging
from pathlib import Path

from cryptography import x509
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives.serialization import pkcs12


class KeyBindingUtility:
    """Keeps one PKCS12 per UIN/VID (file name = hex SHA-1 of the id) in the bound-keys folder.

    The p12 is written only after the key binding succeeds, so its presence means the key is
    already bound for that id and must be reused. It holds the private key and the identity
    certificate returned by IDA.
    """

    def __init__(self, bound_keys_dir, p12_password, logger=None):
        self.bound_keys_dir = Path(bound_keys_dir)
        self.p12_password = p12_password.encode('utf-8')
        self.logger = logger or logging.getLogger(__name__)

    def p12_path(self, idvid: str) -> Path:
        return self.bound_keys_dir / (hashlib.sha1(idvid.encode('utf-8')).hexdigest() + '.p12')

    def list_p12_files(self) -> list:
        if not self.bound_keys_dir.exists():
            return []
        return sorted(self.bound_keys_dir.glob('*.p12'))

    def load_or_generate_key(self, idvid: str):
        """Returns (private_key, reused). Reuses the key from an existing p12 for this id."""
        path = self.p12_path(idvid)
        if path.exists():
            private_key, _, _ = pkcs12.load_key_and_certificates(path.read_bytes(), self.p12_password)
            self.logger.info('Reusing existing bound key: {}'.format(path))
            return private_key, True
        return rsa.generate_private_key(public_exponent=65537, key_size=2048), False

    def load_key_and_certificate(self, idvid: str):
        """Returns (private_key, certificate) from the p12 of this id, or None if the id has no bound key."""
        path = self.p12_path(idvid)
        if not path.exists():
            return None
        private_key, certificate, _ = pkcs12.load_key_and_certificates(path.read_bytes(), self.p12_password)
        return private_key, certificate

    @staticmethod
    def public_key_jwk(private_key) -> dict:
        numbers = private_key.public_key().public_numbers()

        def b64url(value: int) -> str:
            raw = value.to_bytes((value.bit_length() + 7) // 8, 'big')
            return base64.urlsafe_b64encode(raw).decode('utf-8').rstrip('=')

        return {'kty': 'RSA', 'n': b64url(numbers.n), 'e': b64url(numbers.e)}

    def save(self, idvid: str, private_key, certificate_pem: str) -> Path:
        certificate = x509.load_pem_x509_certificate(certificate_pem.encode('utf-8'))
        path = self.p12_path(idvid)
        self.bound_keys_dir.mkdir(parents=True, exist_ok=True)
        data = pkcs12.serialize_key_and_certificates(
            path.stem.encode('utf-8'), private_key, certificate, None,
            serialization.BestAvailableEncryption(self.p12_password))
        path.write_bytes(data)
        return path

    def clear_all(self) -> int:
        files = self.list_p12_files()
        for file in files:
            file.unlink()
        return len(files)
