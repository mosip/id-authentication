package io.mosip.kernel.crypto.jce.util;
import javax.crypto.SecretKey;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

/**
 * 
 * @author M1037717
 * This class will verify and sign the JWT
 *
 */
@Component
public class JWSValidation {
	
	/**
	 * 
	 * @param key
	 * @param payload
	 * @return signature
	 * @throws JoseException
	 */
	public  String jwsSign(String payload,SecretKey key) throws JoseException  {
		JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(payload);
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA512);
		jws.setKey(key);
		jws.setDoKeyValidation(false);
		return jws.getCompactSerialization();
	}
	
	/**
	 * 
	 * @param signKey
	 * @param jwt
	 * @return boolean
	 * @throws JoseException
	 */
	public boolean verifySignature(String jwt,SecretKey signKey) throws JoseException {
		JsonWebSignature jws = new JsonWebSignature();
		jws.setCompactSerialization(jwt);
		jws.setKey(signKey);
		return jws.verifySignature();
	}	

}
