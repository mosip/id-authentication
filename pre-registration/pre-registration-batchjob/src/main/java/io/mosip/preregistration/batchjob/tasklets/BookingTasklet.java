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

import io.mosip.preregistration.batchjob.model.ResponseDto;

@Component
public class BookingTasklet implements Tasklet {

	private RestTemplate restTemplate;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Value("${bookingAvailablity.url}")
	String bookingAvailablityUrl;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

			restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(bookingAvailablityUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<ResponseDto<String>> entity = new HttpEntity<>(headers);

			String uriBuilder = regbuilder.build().encode().toUriString();

			ResponseEntity<ResponseDto> responseEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, entity, ResponseDto.class);
			

		return RepeatStatus.FINISHED;
	}

}
