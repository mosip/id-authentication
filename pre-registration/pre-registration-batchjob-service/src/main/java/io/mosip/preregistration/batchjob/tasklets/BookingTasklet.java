package io.mosip.preregistration.batchjob.tasklets;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.preregistration.batchjob.entity.AvailibityEntity;
import io.mosip.preregistration.batchjob.model.HolidayDto;
import io.mosip.preregistration.batchjob.model.RegistrationCenterDto;
import io.mosip.preregistration.batchjob.model.RegistrationCenterHolidayDto;
import io.mosip.preregistration.batchjob.model.RegistrationCenterResponseDto;
import io.mosip.preregistration.batchjob.model.ResponseDto;
import io.mosip.preregistration.batchjob.repository.PreRegistrationBookingRepository;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

@Component
public class BookingTasklet implements Tasklet {

	private RestTemplate restTemplate;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Value("${bookingAvailablity.url}")
	String bookingAvailablityUrl;

	@Autowired
	private PreRegistrationBookingRepository bookingRepository;

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
