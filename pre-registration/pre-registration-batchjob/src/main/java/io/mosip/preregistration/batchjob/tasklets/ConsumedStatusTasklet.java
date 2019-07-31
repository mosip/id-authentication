/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.tasklets;

import java.time.LocalDateTime;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
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

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.batchjob.model.LoginUser;
import io.mosip.preregistration.batchjob.model.ResponseWrapper;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.config.LoggerConfiguration;

/**
 * This class is a tasklet of batch job to call update status service in batch service.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class ConsumedStatusTasklet implements Tasklet {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${updateConsumedStatus.url}")
	String updateConsumedUrl;

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

	private Logger log = LoggerConfiguration.logConfig(ConsumedStatusTasklet.class);

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {

		try {
			/* Get the token from auth-manager service */
			LoginUser loginUser=new LoginUser();
			loginUser.setAppId(appId);
			loginUser.setPassword(password);
			loginUser.setUserName(userName);
			RequestWrapper<LoginUser> requestWrapper=new RequestWrapper<>();
			requestWrapper.setId(id);
			requestWrapper.setRequest(loginUser);
			requestWrapper.setRequesttime(LocalDateTime.now());
			

			UriComponentsBuilder authBuilder = UriComponentsBuilder.fromHttpUrl(tokenUrl);
			HttpHeaders tokenHeader = new HttpHeaders();
			tokenHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<RequestWrapper<LoginUser>> tokenEntity = new HttpEntity<>(requestWrapper,tokenHeader);

			String tokenUriBuilder = authBuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In UpdateConsumedStatusTasklet to get token with URL- " + tokenUriBuilder);
			ResponseEntity<ResponseWrapper<AuthNResponse>> tokenResponse = restTemplate.exchange(tokenUriBuilder, HttpMethod.POST,
					tokenEntity,new ParameterizedTypeReference<ResponseWrapper<AuthNResponse>>() {
					});

			/* Rest call to Batch service API of expired appointments */
			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(updateConsumedUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.set("Cookie", tokenResponse.getHeaders().get("Set-Cookie").get(0));
			HttpEntity<MainResponseDTO<String>> entity = new HttpEntity<>(headers);

			String uriBuilder = regbuilder.build().encode().toUriString();

			log.info("sessionId", "idType", "id",
					"In UpdateConsumedStatusTasklet method of Batch Service URL- " + uriBuilder);
			restTemplate.exchange(uriBuilder, HttpMethod.PUT, entity, MainResponseDTO.class);

		} catch (Exception e) {
			log.error("Update Consumed Status ", " Tasklet ", " encountered exception ", e.getMessage());
			throw e;
		}

		return RepeatStatus.FINISHED;
	}

}
