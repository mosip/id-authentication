package io.mosip.preregistration.batchjobservices.test.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.batchjobservices.entity.ApplicantDemographic;
import io.mosip.preregistration.batchjobservices.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.batchjobservices.service.ConsumedStatusService;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConsumedStatusServiceTest {
	
	@MockBean
	private BatchServiceDAO batchServiceDAO;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ConsumedStatusService service;

	List<ProcessedPreRegEntity> preRegList = new ArrayList<>();
	ApplicantDemographic demographicEntity =new ApplicantDemographic();
	RegistrationBookingEntity bookingEntity=new RegistrationBookingEntity();
	
	@Test
	public void consumedAppointmentTest() {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		
		logger.info("demographicEntity " + demographicEntity);
		logger.info("bookingEntity " + bookingEntity);
		
		Mockito.when(batchServiceDAO.getAllConsumedPreIds(Mockito.anyString())).thenReturn(preRegList);
		Mockito.when(batchServiceDAO.getApplicantDemographicDetails(Mockito.anyString())).thenReturn(demographicEntity);
		demographicEntity.setStatusCode("Consumed");
		
		Mockito.when(batchServiceDAO.updateApplicantDemographic(demographicEntity)).thenReturn(true);
		Mockito.when(batchServiceDAO.getPreRegId(Mockito.anyString())).thenReturn(bookingEntity);
		bookingEntity.setStatusCode("Consumed");
		
		Mockito.when(batchServiceDAO.updateBooking(bookingEntity)).thenReturn(true);
		//preRegList.get(0).setStatusCode("NEW_STATUS_COMMENT");
		
		//Mockito.when(batchServiceDAO.updateProcessedList(preRegList.)).thenReturn(true);
		
		response=service.demographicConsumedStatus();
		assertEquals("Demographic status to consumed updated successfully", response.getResponse());
		
	}
}
