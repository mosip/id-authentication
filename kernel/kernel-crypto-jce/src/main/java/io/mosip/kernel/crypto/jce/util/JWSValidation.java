package io.mosip.kernel.crypto.jce.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.JwsSpec;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * 
 * @author M1037717 This class will verify and sign the JWT
 *
 */
@Component
public class JWSValidation implements JwsSpec<String, String, X509Certificate,PrivateKey> {

	/** The public key. */
	protected PublicKey publicKey;

	/**
	 * 
	 * @param key
	 * @param payload
	 * @return signature
	 * @throws JoseException
	 */
	@Override
	public String jwsSign(String payload, PrivateKey pKey) {
		try {
			JsonWebSignature jws = new JsonWebSignature();
			jws.setPayload(HMACUtils.digestAsPlainText(HMACUtils.generateHash(payload.getBytes())));
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
			jws.setKey(pKey);
			jws.setDoKeyValidation(false);
			return jws.getCompactSerialization();
		} catch (JoseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param key
	 * @param sign
	 * @return boolean
	 */
	@Override
	public boolean verifySignature(String sign, X509Certificate certificate) {
		JsonWebSignature jws = new JsonWebSignature();
		try {
			certificate.checkValidity();
			publicKey = certificate.getPublicKey();
			certificate.verify(publicKey);
			jws.setKey(publicKey);
			jws.setCompactSerialization(sign);
			jws.setKey(publicKey);
			return jws.verifySignature();
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException | JoseException e) {
			e.printStackTrace();
		}
		return false;

	}

}
