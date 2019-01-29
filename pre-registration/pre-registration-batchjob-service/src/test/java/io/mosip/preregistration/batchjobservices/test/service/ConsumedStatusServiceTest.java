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
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingPK;
import io.mosip.preregistration.batchjobservices.repository.DemographicRepository;
import io.mosip.preregistration.batchjobservices.repository.ProcessedPreIdRepository;
import io.mosip.preregistration.batchjobservices.repository.RegAppointmentRepository;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.batchjobservices.service.ConsumedStatusService;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConsumedStatusServiceTest {

	@Autowired
	private BatchServiceDAO batchServiceDAO;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ConsumedStatusService service;

	@MockBean
	private ProcessedPreIdRepository preIdRepository;

	@MockBean
	private DemographicRepository demographicRepository;

	@MockBean
	private RegAppointmentRepository regAppointmentRepository;
	
	private static final String STATUS_COMMENTS = "Processed by registration processor";

	List<ProcessedPreRegEntity> preRegList = new ArrayList<>();
	ApplicantDemographic demographicEntity = new ApplicantDemographic();
	RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
	RegistrationBookingPK bookingPK = new RegistrationBookingPK();
	ProcessedPreRegEntity processedEntity = new ProcessedPreRegEntity();

	@Test
	public void consumedAppointmentTest() {
		MainResponseDTO<String> response = new MainResponseDTO<>();

		demographicEntity.setPreRegistrationId("12345678909876");

		bookingPK.setPreregistrationId("12345678909876");
		bookingEntity.setBookingPK(bookingPK);

		processedEntity.setPreRegistrationId("12345678909876");
		processedEntity.setStatusCode("Consumed");
		processedEntity.setStatusComments(STATUS_COMMENTS);

		preRegList.add(processedEntity);

		logger.info("demographicEntity " + demographicEntity);
		logger.info("bookingEntity " + bookingEntity);
		Mockito.when(preIdRepository.findBystatusComments(STATUS_COMMENTS))
	    		.thenReturn(preRegList);
		Mockito.when(demographicRepository.findBypreRegistrationId(demographicEntity.getPreRegistrationId()))
				.thenReturn(demographicEntity);
		demographicEntity.setStatusCode("Consumed");
		Mockito.when(demographicRepository.save(demographicEntity)).thenReturn(demographicEntity);
		Mockito.when(regAppointmentRepository.getPreRegId("12345678909876")).thenReturn(bookingEntity);
		bookingEntity.setStatusCode("Consumed");
		Mockito.when(regAppointmentRepository.save(bookingEntity)).thenReturn(bookingEntity);

		Mockito.when(preIdRepository.save(Mockito.any())).thenReturn(processedEntity);

		// preRegList.get(0).setStatusCode("NEW_STATUS_COMMENT");

		// Mockito.when(batchServiceDAO.updateProcessedList(preRegList.)).thenReturn(true);

		response = service.demographicConsumedStatus();
		assertEquals("Demographic status to consumed updated successfully", response.getResponse());

	}
}
