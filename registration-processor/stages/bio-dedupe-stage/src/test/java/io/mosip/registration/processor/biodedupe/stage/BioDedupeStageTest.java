package io.mosip.registration.processor.biodedupe.stage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DataAccessException;

import io.mosip.registration.processor.bio.dedupe.exception.ABISAbortException;
import io.mosip.registration.processor.bio.dedupe.exception.ABISInternalError;
import io.mosip.registration.processor.bio.dedupe.exception.UnableToServeRequestABISException;
import io.mosip.registration.processor.bio.dedupe.exception.UnexceptedError;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.ResponseStatusCode;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

/**
 * The Class BioDedupeStageTest.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class BioDedupeStageTest {

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The bio dedupe service. */
	@Mock
	private BioDedupeService bioDedupeService;

	/** The dto. */
	MessageDTO dto = new MessageDTO();

	/** The vertx. */
	private Vertx vertx;

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The matched reg ids. */
	List<String> matchedRegIds = new ArrayList<String>();

	/** The bio dedupe stage. */
	@InjectMocks
	private BioDedupeStage bioDedupeStage = new BioDedupeStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String url) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
	};

	/**
	 * Test deploy verticle.
	 */
	@Test
	public void testDeployVerticle() {
		bioDedupeStage.deployVerticle();
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {

		AuditResponseDto auditResponseDto = new AuditResponseDto();
		Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_405.toString(), EventName.UPDATE.toString(),
				EventType.BUSINESS.toString(), "1234testcase");
		dto.setRid("reg1234");
		registrationStatusDto.setRegistrationId("reg1234");

	}

	/**
	 * Test bio dedupe success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testBioDedupeSuccess() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.when(bioDedupeService.insertBiometrics(anyString())).thenReturn(ResponseStatusCode.SUCCESS.name());
		Mockito.when(bioDedupeService.performDedupe(anyString())).thenReturn(matchedRegIds);
		doNothing().when(packetInfoManager).saveManualAdjudicationData(matchedRegIds, "reg1234");

		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertTrue(messageDto.getIsValid());

	}

	/**
	 * Test bio dedupe failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testBioDedupeFailure() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		matchedRegIds.add("4567");
		Mockito.when(bioDedupeService.insertBiometrics(anyString())).thenReturn(ResponseStatusCode.SUCCESS.name());
		Mockito.when(bioDedupeService.performDedupe(anyString())).thenReturn(matchedRegIds);
		doNothing().when(packetInfoManager).saveManualAdjudicationData(matchedRegIds, "reg1234");

		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertFalse(messageDto.getIsValid());

	}

	/**
	 * Test bio dedupe insertion failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testBioDedupeInsertionFailure() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		matchedRegIds.add("4567");
		Mockito.when(bioDedupeService.insertBiometrics(anyString())).thenReturn(ResponseStatusCode.FAILURE.name());
		Mockito.when(bioDedupeService.performDedupe(anyString())).thenReturn(matchedRegIds);
		doNothing().when(packetInfoManager).saveManualAdjudicationData(matchedRegIds, "reg1234");

		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertFalse(messageDto.getIsValid());

	}

	/**
	 * Test apis resource access exception.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testApisResourceAccessException() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		ApisResourceAccessException exp = new ApisResourceAccessException("errorMessage");
		Mockito.doThrow(exp).when(bioDedupeService).insertBiometrics(anyString());

		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Test unexcepted error.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testUnexceptedError() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		UnexceptedError exp = new UnexceptedError("errorMessage");
		matchedRegIds.add("4567");
		Mockito.when(bioDedupeService.insertBiometrics(anyString())).thenReturn(ResponseStatusCode.SUCCESS.name());
		Mockito.doThrow(exp).when(bioDedupeService).performDedupe(anyString());
		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Test unable to serve request ABIS exception.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testUnableToServeRequestABISException() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		UnableToServeRequestABISException exp = new UnableToServeRequestABISException("errorMessage");
		matchedRegIds.add("4567");
		Mockito.when(bioDedupeService.insertBiometrics(anyString())).thenReturn(ResponseStatusCode.SUCCESS.name());
		Mockito.doThrow(exp).when(bioDedupeService).performDedupe(anyString());
		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Test ABIS abort exception.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testABISAbortException() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		ABISAbortException exp = new ABISAbortException("errorMessage");
		matchedRegIds.add("4567");
		Mockito.when(bioDedupeService.insertBiometrics(anyString())).thenReturn(ResponseStatusCode.SUCCESS.name());
		Mockito.doThrow(exp).when(bioDedupeService).performDedupe(anyString());
		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Test ABIS internal error.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testABISInternalError() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		ABISInternalError exp = new ABISInternalError("errorMessage");
		matchedRegIds.add("4567");
		Mockito.when(bioDedupeService.insertBiometrics(anyString())).thenReturn(ResponseStatusCode.SUCCESS.name());
		Mockito.doThrow(exp).when(bioDedupeService).performDedupe(anyString());
		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Data access exception test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void dataAccessExceptionTest() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.when(bioDedupeService.insertBiometrics(anyString())).thenThrow(new DataAccessException("") {
		});

		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	@Test
	public void exceptionTest() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.when(bioDedupeService.insertBiometrics(anyString())).thenThrow(new NullPointerException());
		MessageDTO messageDto = bioDedupeStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}
}
