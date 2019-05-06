package io.mosip.preregistration.batchjobservices.test.service;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.batchjobservices.entity.DemographicEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingPK;
import io.mosip.preregistration.batchjobservices.repository.DemographicRepository;
import io.mosip.preregistration.batchjobservices.repository.RegAppointmentRepository;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.batchjobservices.service.ExpiredStatusService;
import io.mosip.preregistration.batchjobservices.test.BatchJobApplicationTest;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

@SpringBootTest(classes = { BatchJobApplicationTest.class })
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ExpiredStatusServiceTest {

	@Autowired
	private BatchServiceDAO batchServiceDAO;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ExpiredStatusService service;
	
	@MockBean
	private DemographicRepository demographicRepository;

	@MockBean
	private RegAppointmentRepository regAppointmentRepository;

	LocalDate currentDate = LocalDate.now();
	List<RegistrationBookingEntity> bookedPreIdList = new ArrayList<>();
	DemographicEntity demographicEntity =new DemographicEntity();
	RegistrationBookingEntity bookingEntity=new RegistrationBookingEntity();
	RegistrationBookingPK bookingPK = new RegistrationBookingPK();
	
	@Test
	public void expiredAppointmentTest() {

		MainResponseDTO<String> response = new MainResponseDTO<>();
 
		demographicEntity.setPreRegistrationId("12345678909876");
		demographicEntity.setStatusCode(StatusCodes.BOOKED.getCode());

		bookingPK.setPreregistrationId("12345678909876");
		bookingEntity.setBookingPK(bookingPK);
		bookingEntity.setRegDate(LocalDate.parse("2018-12-04"));
		bookingEntity.setSlotFromTime(LocalTime.parse("09:00"));
		
		bookedPreIdList.add(bookingEntity);
		logger.info("demographicEntity " + demographicEntity);
		logger.info("bookingEntity " + bookingEntity);
		
		Mockito.when(regAppointmentRepository.findByRegDateBefore(currentDate)).thenReturn(bookedPreIdList);
		Mockito.when(regAppointmentRepository.getPreRegId(bookingEntity.getBookingPK().getPreregistrationId())).thenReturn(bookingEntity);
		//bookingEntity.setStatusCode("EXPIRED");
		Mockito.when(regAppointmentRepository.save(bookingEntity)).thenReturn(bookingEntity);
		Mockito.when(demographicRepository.findBypreRegistrationId(demographicEntity.getPreRegistrationId())).thenReturn(demographicEntity);
		//demographicEntity.setStatusCode("EXPIRED");
		Mockito.when(demographicRepository.save(demographicEntity)).thenReturn(demographicEntity);
		
		response=service.expireAppointments();
		assertEquals("Registration appointment status updated to expired successfully",response.getResponse());
	}

}
