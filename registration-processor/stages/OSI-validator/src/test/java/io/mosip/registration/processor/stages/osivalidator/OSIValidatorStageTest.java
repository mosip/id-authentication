package io.mosip.registration.processor.stages.osivalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DataAccessException;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class OSIValidatorStageTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonUtil.class, IOUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class OSIValidatorStageTest {

	/** The osi validator stage. */
	@InjectMocks
	private OSIValidatorStage osiValidatorStage = new OSIValidatorStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String clusterAddress, String localhost) {
			return null;
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
		osiValidatorStage.deployVerticle();
	}

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The o SI validator. */
	@Mock
	private OSIValidator oSIValidator;

	/** The umc validator. */
	@Mock
	UMCValidator umcValidator;

	/** The dto. */
	MessageDTO dto = new MessageDTO();

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {

		@SuppressWarnings("unchecked")
		RegistrationProcessorRestClientService<Object> mockObj = Mockito
				.mock(RegistrationProcessorRestClientService.class);

		Field auditLog = AuditLogRequestBuilder.class.getDeclaredField("registrationProcessorRestService");
		auditLog.setAccessible(true);
		auditLog.set(auditLogRequestBuilder, mockObj);
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_401.toString(), EventName.ADD.toString(),
				EventType.BUSINESS.toString(), "1234testcase");

		dto.setRid("reg1234");
		registrationStatusDto.setRegistrationId("reg1234");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);

	}

	/**
	 * Testis valid OSI success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testisValidOSISuccess() throws Exception {

		Mockito.when(oSIValidator.isValidOSI((anyString()))).thenReturn(Boolean.TRUE);
		Mockito.when(umcValidator.isValidUMC((anyString()))).thenReturn(Boolean.TRUE);
		MessageDTO messageDto = osiValidatorStage.process(dto);

		assertTrue(messageDto.getIsValid());

	}

	/**
	 * Testis valid OSI failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testisValidOSIFailure() throws Exception {
		Mockito.when(oSIValidator.isValidOSI((anyString()))).thenReturn(Boolean.FALSE);
		Mockito.when(umcValidator.isValidUMC((anyString()))).thenReturn(Boolean.TRUE);

		MessageDTO messageDto = osiValidatorStage.process(dto);

		assertFalse(messageDto.getIsValid());
	}

	/**
	 * IO exception test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void IOExceptionTest() throws Exception {
		Mockito.when(umcValidator.isValidUMC((anyString()))).thenReturn(Boolean.TRUE);
		Mockito.when(oSIValidator.isValidOSI((anyString()))).thenThrow(new IOException());
		MessageDTO messageDto = osiValidatorStage.process(dto);
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
		Mockito.when(umcValidator.isValidUMC((anyString()))).thenReturn(Boolean.TRUE);
		Mockito.when(oSIValidator.isValidOSI((anyString()))).thenThrow(new DataAccessException("") {
		});
		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Exception test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void exceptionTest() throws Exception {
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(null);
		Mockito.when(oSIValidator.isValidOSI((anyString()))).thenReturn(false);
		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Testis valid OSI failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testisValidOSIFailureWithRetryCount() throws Exception {

		registrationStatusDto.setRetryCount(1);
		Mockito.when(oSIValidator.isValidOSI((anyString()))).thenReturn(Boolean.FALSE);

		MessageDTO messageDto = osiValidatorStage.process(dto);

		assertFalse(messageDto.getIsValid());
	}

}
