package io.mosip.preregistration.batchjobservices.test.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.preregistration.batchjobservices.entity.DemographicEntityConsumed;
import io.mosip.preregistration.batchjobservices.entity.DocumentEntityConsumed;
import io.mosip.preregistration.batchjobservices.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntityConsumed;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingPKConsumed;
import io.mosip.preregistration.batchjobservices.repository.DemographicConsumedRepository;
import io.mosip.preregistration.batchjobservices.repository.DemographicRepository;
import io.mosip.preregistration.batchjobservices.repository.DocumentConsumedRepository;
import io.mosip.preregistration.batchjobservices.repository.DocumentRespository;
import io.mosip.preregistration.batchjobservices.repository.ProcessedPreIdRepository;
import io.mosip.preregistration.batchjobservices.repository.RegAppointmentConsumedRepository;
import io.mosip.preregistration.batchjobservices.repository.RegAppointmentRepository;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.batchjobservices.service.ConsumedStatusService;
import io.mosip.preregistration.batchjobservices.test.BatchJobApplicationTest;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.common.entity.DocumentEntity;
import io.mosip.preregistration.core.common.entity.RegistrationBookingEntity;
import io.mosip.preregistration.core.common.entity.RegistrationBookingPK;
import io.mosip.preregistration.core.util.AuditLogUtil;

@SpringBootTest(classes = { BatchJobApplicationTest.class })
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ConsumedStatusServiceTest {

	@Autowired
	private BatchServiceDAO batchServiceDAO;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ConsumedStatusService service;
	@MockBean
	AuditLogUtil auditLogUtil;

	/**
	 * MockBean reference for {@link #demographicRepository}
	 */
	@MockBean
	@Qualifier("demographicRepository")
	private DemographicRepository demographicRepository;

	/**
	 * MockBean reference for {@link #demographicConsumedRepository}
	 */
	@MockBean
	@Qualifier("demographicConsumedRepository")
	private DemographicConsumedRepository demographicConsumedRepository;

	/**
	 * MockBean reference for {@link #regAppointmentRepository}
	 */
	@MockBean
	@Qualifier("regAppointmentRepository")
	private RegAppointmentRepository regAppointmentRepository;

	/**
	 * MockBean reference for {@link #processedPreIdRepository}
	 */
	@MockBean
	@Qualifier("processedPreIdRepository")
	private ProcessedPreIdRepository processedPreIdRepository;

	/**
	 * MockBean reference for {@link #appointmentConsumedRepository}
	 */
	@MockBean
	@Qualifier("regAppointmentConsumedRepository")
	private RegAppointmentConsumedRepository appointmentConsumedRepository;

	/**
	 * MockBean reference for {@link #documentRespository}
	 */
	@MockBean
	@Qualifier("documentRespository")
	private DocumentRespository documentRespository;

	/**
	 * MockBean reference for {@link #documentConsumedRepository}
	 */
	@MockBean
	@Qualifier("documentConsumedRepository")
	private DocumentConsumedRepository documentConsumedRepository;

	@MockBean
	private ProcessedPreIdRepository preIdRepository;

	private static final String STATUS_COMMENTS = "Processed by registration processor";

	List<ProcessedPreRegEntity> preRegList = new ArrayList<>();
	DemographicEntity demographicEntity = new DemographicEntity();
	DemographicEntityConsumed demographicEntityConsumed = new DemographicEntityConsumed();

	List<DocumentEntity> documentEntityList = new ArrayList<>();
	DocumentEntityConsumed documentEntityConsumed = new DocumentEntityConsumed();

	RegistrationBookingEntity bookingEntity = new RegistrationBookingEntity();
	RegistrationBookingPK bookingPK = new RegistrationBookingPK();
	RegistrationBookingEntityConsumed bookingEntityConsumed = new RegistrationBookingEntityConsumed();
	RegistrationBookingPKConsumed bookingPKConsumed = new RegistrationBookingPKConsumed();
	ProcessedPreRegEntity processedEntity = new ProcessedPreRegEntity();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		AuthUserDetails applicationUser = Mockito.mock(AuthUserDetails.class);
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
	}

	@Test
	public void consumedAppointmentTest() {
		MainResponseDTO<String> response = new MainResponseDTO<>();

		byte[] encryptedDemographicDetails = { 1, 0, 1, 0, 1, 0 };
		String preregId = "12345678909876";
		demographicEntity.setPreRegistrationId(preregId);
		demographicEntity.setApplicantDetailJson(encryptedDemographicDetails);
		DocumentEntity documentEntity = new DocumentEntity();
		DemographicEntity demographicEntity = new DemographicEntity();
		demographicEntity.setPreRegistrationId(preregId);
		documentEntity.setDemographicEntity(demographicEntity);
		documentEntityList.add(documentEntity);
		documentEntityConsumed.setPreregId(preregId);

		bookingEntity.setDemographicEntity(demographicEntity);
		bookingEntity.setBookingPK(bookingPK);

		bookingPKConsumed.setPreregistrationId(preregId);
		bookingEntityConsumed.setBookingPK(bookingPKConsumed);

		processedEntity.setPreRegistrationId(preregId);
		processedEntity.setStatusCode("Consumed");
		processedEntity.setStatusComments(STATUS_COMMENTS);

		preRegList.add(processedEntity);

		logger.info("demographicEntity " + demographicEntity);
		logger.info("bookingEntity " + bookingEntity);
		Mockito.when(preIdRepository.findBystatusComments(STATUS_COMMENTS)).thenReturn(preRegList);
		Mockito.when(demographicRepository.findBypreRegistrationId(demographicEntity.getPreRegistrationId()))
				.thenReturn(demographicEntity);
		// BeanUtils.copyProperties(demographicEntity, demographicEntityConsumed);
		demographicEntityConsumed.setPreRegistrationId(preregId);
		demographicEntityConsumed.setApplicantDetailJson(encryptedDemographicDetails);
		demographicEntityConsumed.setStatusCode(StatusCodes.CONSUMED.getCode());
		Mockito.when(demographicConsumedRepository.save(demographicEntityConsumed))
				.thenReturn(demographicEntityConsumed);
		Mockito.when(documentRespository.findByDemographicEntityPreRegistrationId(preregId))
				.thenReturn(documentEntityList);
		// BeanUtils.copyProperties(documentEntity, documentEntityConsumed);
		documentEntityConsumed.setPreregId(preregId);
		Mockito.when(documentConsumedRepository.save(documentEntityConsumed)).thenReturn(documentEntityConsumed);
		Mockito.when(regAppointmentRepository.getDemographicEntityPreRegistrationId(preregId))
				.thenReturn(bookingEntity);
		// BeanUtils.copyProperties(bookingEntity, bookingEntityConsumed);
		RegistrationBookingPKConsumed bkc = new RegistrationBookingPKConsumed();
		bkc.setPreregistrationId(preregId);
		bookingEntityConsumed.setBookingPK(bkc);
		Mockito.when(appointmentConsumedRepository.save(bookingEntityConsumed)).thenReturn(bookingEntityConsumed);

		response = service.demographicConsumedStatus();
		assertEquals("Demographic status to consumed updated successfully", response.getResponse());

	}

	@Test
	public void consumedAppointmentFailureTest() {
		MainResponseDTO<String> response = new MainResponseDTO<>();

		// byte[] encryptedDemographicDetails = { 1, 0, 1, 0, 1, 0 };
		String preregId = "12345678909876";
		demographicEntity.setPreRegistrationId(preregId);
		// demographicEntity.setApplicantDetailJson(encryptedDemographicDetails);
		DocumentEntity documentEntity = new DocumentEntity();
		documentEntity.setDemographicEntity(demographicEntity);
		documentEntityList.add(documentEntity);
		documentEntityConsumed.setPreregId(preregId);

		bookingEntity.setBookingPK(bookingPK);
		bookingEntity.setDemographicEntity(demographicEntity);
		bookingPKConsumed.setPreregistrationId(preregId);
		bookingEntityConsumed.setBookingPK(bookingPKConsumed);

		processedEntity.setPreRegistrationId(preregId);
		processedEntity.setStatusCode("Consumed");
		processedEntity.setStatusComments(STATUS_COMMENTS);

		preRegList.add(processedEntity);

		logger.info("demographicEntity " + demographicEntity);
		logger.info("bookingEntity " + bookingEntity);
		Mockito.when(preIdRepository.findBystatusComments(STATUS_COMMENTS)).thenReturn(preRegList);
		Mockito.when(demographicRepository.findBypreRegistrationId(demographicEntity.getPreRegistrationId()))
				.thenReturn(demographicEntity);
		// BeanUtils.copyProperties(demographicEntity, demographicEntityConsumed);
		demographicEntityConsumed.setPreRegistrationId(preregId);
		// demographicEntityConsumed.setApplicantDetailJson(encryptedDemographicDetails);
		demographicEntityConsumed.setStatusCode(StatusCodes.CONSUMED.getCode());
		Mockito.when(demographicConsumedRepository.save(demographicEntityConsumed))
				.thenReturn(demographicEntityConsumed);
		Mockito.when(documentRespository.findByDemographicEntityPreRegistrationId(preregId))
				.thenReturn(documentEntityList);
		// BeanUtils.copyProperties(documentEntity, documentEntityConsumed);
		documentEntityConsumed.setPreregId(preregId);
		Mockito.when(documentConsumedRepository.save(documentEntityConsumed)).thenReturn(documentEntityConsumed);
		Mockito.when(regAppointmentRepository.getDemographicEntityPreRegistrationId(preregId))
				.thenReturn(bookingEntity);
		// BeanUtils.copyProperties(bookingEntity, bookingEntityConsumed);
		RegistrationBookingPKConsumed bkc = new RegistrationBookingPKConsumed();
		bkc.setPreregistrationId(preregId);
		bookingEntityConsumed.setBookingPK(bkc);
		Mockito.when(appointmentConsumedRepository.save(bookingEntityConsumed)).thenReturn(bookingEntityConsumed);

		response = service.demographicConsumedStatus();
		assertEquals("Demographic status to consumed updated successfully", response.getResponse());

	}
}
