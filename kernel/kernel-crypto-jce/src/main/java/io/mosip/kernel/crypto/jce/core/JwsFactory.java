package io.mosip.kernel.crypto.jce.core;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import io.mosip.kernel.core.crypto.spi.JwsSpec;
import io.mosip.kernel.crypto.jce.util.JWSValidation;

public class JwsFactory {
	
	public JwsSpec<String, String , X509Certificate,PrivateKey> getJWS(){
		return new JWSValidation();
	}

}
