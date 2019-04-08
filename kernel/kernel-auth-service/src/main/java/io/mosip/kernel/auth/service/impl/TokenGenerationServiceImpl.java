/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.entities.AuthNResponseDto;
import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.service.AuthService;
import io.mosip.kernel.auth.service.TokenGenerationService;

/**
 * @author M1049825
 *
 */
@Component
public class TokenGenerationServiceImpl implements TokenGenerationService {
	
	@Autowired
	AuthService authService;
	
	@Value("${mosip.kernel.auth.app.id}")
	private String authAppId;
	
	@Value("${mosip.kernel.auth.client.id}")
	private String clientId;
	
	@Value("${mosip.kernel.auth.secret.key}")
	private String secretKey;

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.TokenGenerationService#getInternalTokenGenerationService()
	 */
	@Override
	public String getInternalTokenGenerationService() throws Exception {
		ClientSecret clientSecret = new ClientSecret();
		clientSecret.setAppId(authAppId);
		clientSecret.setClientId(clientId);
		clientSecret.setSecretKey(secretKey);
		AuthNResponseDto authNResponseDto = authService.authenticateWithSecretKey(clientSecret);
		return authNResponseDto.getToken();
	}

}
