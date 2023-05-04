package io.mosip.authentication.esignet.integration.helper;

import java.util.Objects;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class IdentityDataCache {
	
	public void putEncryptedIdentityData(String kycToken, String psut, String encryptedIdentityData) {
		Objects.requireNonNull(encryptedIdentityData);
		internalCacheData(kycToken, psut, encryptedIdentityData);
	}
	
	public String getEncryptedIdentityData(String kycToken, String psut) {
		return internalCacheData(kycToken, psut, null);
	}
	
	@Cacheable(value="encryptedIdentityData", key="#kycToken + #psut")
	private String internalCacheData(String kycToken, String psut, String encryptedIdentityData) {
		return encryptedIdentityData;		
	}
	

}
