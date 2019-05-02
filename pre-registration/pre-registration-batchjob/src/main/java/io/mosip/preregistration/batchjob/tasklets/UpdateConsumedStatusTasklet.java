package io.mosip.preregistration.batchjob.tasklets;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;

/**
 * @author M1043008
 *
 */
@Component
public class UpdateConsumedStatusTasklet implements Tasklet {
	
	private RestTemplate restTemplate;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Value("${updateConsumedStatus.url}")
	String updateConsumedUrl;
	
	private Logger log = LoggerConfiguration.logConfig(UpdateConsumedStatusTasklet.class);
	

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		
		restTemplate = restTemplateBuilder.build();
		UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(updateConsumedUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<MainResponseDTO<String>> entity = new HttpEntity<>(headers);

		String uriBuilder = regbuilder.build().encode().toUriString();

		log.info("sessionId", "idType", "id", "In UpdateConsumedStatusTasklet method of Batch Service URL- "+uriBuilder);
		ResponseEntity<MainResponseDTO> responseEntity = restTemplate.exchange(uriBuilder,
				HttpMethod.PUT, entity, MainResponseDTO.class);
		return RepeatStatus.FINISHED;
	}

}
