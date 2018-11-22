package io.mosip.preregistration.application.test.exception;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.preregistration.application.code.StateManagment;
import io.mosip.preregistration.application.dto.ResponseDto;
import io.mosip.preregistration.application.entity.PreRegistrationEntity;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.repository.PreRegistrationRepository;
import io.mosip.preregistration.application.service.PreRegistrationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentFailedToDeleteTest {

	@Autowired
	private PreRegistrationService preRegistrationService;

	@MockBean
	private PreRegistrationRepository preRegistrationRepository;

	private ResponseEntity<ResponseDto> responseEntity;
	
	private HttpEntity<ResponseDto> entity;
	
	@MockBean
	RestTemplateBuilder restTemplateBuilder;
	
	
	private RestTemplate restTemplate;
	String preregId = "12345";
	UriComponentsBuilder builder ;
	@Before
	public void setUp() {
		restTemplate=Mockito.mock(RestTemplate.class);
		HttpHeaders headers = new HttpHeaders();

		entity = new HttpEntity<>(headers);

		 builder = Mockito.mock(UriComponentsBuilder.class);
		//String uriBuilder = builder.build().encode().toUriString();

		responseEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	@Test(expected = DocumentFailedToDeleteException.class)
	public void failedToDeleteTest() {
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		PreRegistrationEntity  preRegistrationEntity= new PreRegistrationEntity();
		
		preRegistrationEntity.setStatusCode(StateManagment.Pending_Appointment.name());
		Mockito.when(preRegistrationRepository.findBypreRegistrationId(preregId)).thenReturn(preRegistrationEntity);
		

	
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(HttpEntity.class),
				Mockito.eq(ResponseDto.class))).thenReturn(responseEntity);
		
		preRegistrationService.deleteIndividual(preregId);
//		Mockito.when(preRegistrationService.deleteIndividual(preregId))
//				.thenThrow(DocumentFailedToDeleteException.class);

	}
}
