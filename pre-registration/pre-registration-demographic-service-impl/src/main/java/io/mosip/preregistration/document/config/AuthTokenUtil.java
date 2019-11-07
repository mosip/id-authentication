package io.mosip.preregistration.document.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.document.dto.LoginUser;
import io.mosip.preregistration.document.exception.LoginServiceException;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class AuthTokenUtil {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.batch.token.authmanager.url}")
	String tokenUrl;
	@Value("${mosip.batch.token.request.id}")
	String id;
	@Value("${mosip.batch.token.authmanager.appId}")
	String appId;
	@Value("${mosip.batch.token.authmanager.userName}")
	String userName;
	@Value("${mosip.batch.token.authmanager.password}")
	String password;

	@Value("${version}")
	String version;

	private Logger log = LoggerConfiguration.logConfig(AuthTokenUtil.class);

	public HttpHeaders getTokenHeader() {

		HttpHeaders headers = new HttpHeaders();
		try {
			/* Get the token from auth-manager service */
			LoginUser loginUser = new LoginUser();
			loginUser.setAppId(appId);
			loginUser.setPassword(password);
			loginUser.setUserName(userName);
			RequestWrapper<LoginUser> requestWrapper = new RequestWrapper<>();
			requestWrapper.setId(id);
			requestWrapper.setRequest(loginUser);
			requestWrapper.setRequesttime(LocalDateTime.now());

			UriComponentsBuilder authBuilder = UriComponentsBuilder.fromHttpUrl(tokenUrl);
			HttpHeaders tokenHeader = new HttpHeaders();
			tokenHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<RequestWrapper<LoginUser>> tokenEntity = new HttpEntity<>(requestWrapper, tokenHeader);

			String tokenUriBuilder = authBuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id", "In BookingTasklet to get token with URL- " + tokenUriBuilder);
			ResponseEntity<ResponseWrapper<AuthNResponse>> tokenResponse = restTemplate.exchange(tokenUriBuilder,
					HttpMethod.POST, tokenEntity, new ParameterizedTypeReference<ResponseWrapper<AuthNResponse>>() {
					});
			if (tokenResponse.getBody().getErrors() != null) {

				log.error("Sync master ", " Authmanager ", " encountered exception ",
						tokenResponse.getBody().getErrors().get(0).getMessage());
				throw new LoginServiceException(tokenResponse.getBody().getErrors());
			}
			/* Call to availability sync Util */

			headers.set("Cookie", tokenResponse.getHeaders().get("Set-Cookie").get(0));

		} catch (Exception e) {
			log.error("Sync master ", " Tasklet ", " encountered exception ", e.getMessage());
			throw e;
		}
		return headers;
	}

}
