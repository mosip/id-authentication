package io.mosip.registration.processor.stages.osivalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonUtil.class, IOUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class OSIValidatorStageTest {

	@Mock
	private InputStream inputStream;

	@Mock
	FilesystemCephAdapterImpl adapter;

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

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

	@Test
	public void testDeployVerticle() {
		osiValidatorStage.deployVerticle();
	}

	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	PacketInfoManager<Identity, RegOsiDto> packetInfoManager;

	@Mock
	AuthResponseDTO authResponseDTO = new AuthResponseDTO();
	byte[] data = "1234567890".getBytes();

	@InjectMocks
	private OSIValidator oSIValidator;

	private RegOsiDto regOsiDto = new RegOsiDto();

	MessageDTO dto = new MessageDTO();
	InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	@Before
	public void setUp() throws Exception {

		regOsiDto.setOfficerId("O1234");
		regOsiDto.setOfficerFingerpImageName("fingerprint");
		regOsiDto.setOfficerfingerType("RIGHTRING");
		regOsiDto.setOfficerIrisImageName(null);
		regOsiDto.setOfficerIrisType("LEFTEYE");
		regOsiDto.setOfficerPhotoName(null);
		regOsiDto.setOfficerHashedPin("officerHashedPin");

		regOsiDto.setSupervisorId("S1234");
		regOsiDto.setSupervisorFingerpImageName("supervisorFingerpImageName");
		regOsiDto.setSupervisorFingerType("LEFTINDEX");
		regOsiDto.setSupervisorIrisImageName("supervisorIrisImageName");
		regOsiDto.setSupervisorIrisType("LEFTEYE");
		regOsiDto.setSupervisorPhotoName("supervisorPhotoName");

		regOsiDto.setIntroducerUin("I1234");
		regOsiDto.setIntroducerFingerpImageName("introducerFingerpImageName");
		regOsiDto.setIntroducerFingerpType("RIGHTRING");
		regOsiDto.setIntroducerIrisImageName("IntroducerIrisImageName");
		regOsiDto.setIntroducerIrisType("RIGHTEYE");

		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		Mockito.when(adapter.checkFileExistence(anyString(), anyString())).thenReturn(true);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		authResponseDTO.setStatus(true);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

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

	@Test
	public void testisValidOSISuccess() throws Exception {

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);

		MessageDTO messageDto = osiValidatorStage.process(dto);

		assertTrue(messageDto.getIsValid());

	}

	@Test
	public void testOfficeIntroIdNull() throws Exception {
		regOsiDto.setOfficerId(null);
		regOsiDto.setIntroducerUin(null);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());
	}

	@Test
	public void testSupervisorIdNull() throws Exception {
		regOsiDto.setSupervisorId(null);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		MessageDTO messageDto = osiValidatorStage.process(dto);

		assertFalse(messageDto.getIsValid());
	}

	@Test
	public void testSupervisorDetailsNull() throws Exception {
		regOsiDto.setSupervisorFingerpImageName(null);
		regOsiDto.setSupervisorIrisImageName(null);
		regOsiDto.setSupervisorPhotoName(null);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());
	}

	@Test
	public void testOfficerDetailsNull() throws Exception {
		regOsiDto.setOfficerFingerpImageName(null);
		regOsiDto.setOfficerIrisImageName(null);
		regOsiDto.setOfficerPhotoName(null);
		regOsiDto.setOfficerHashedPin(null);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());
	}

	@Test
	public void testIntroducerDetailsNull() throws Exception {
		regOsiDto.setIntroducerFingerpImageName(null);
		regOsiDto.setIntroducerIrisImageName(null);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());
	}

	@Test
	public void IOExceptionTest() throws Exception {

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenThrow(new IOException());

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertEquals(true, messageDto.getInternalError());

	}

	@Test
	public void testisValidOSIFailure() throws Exception {
		authResponseDTO.setStatus(false);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());
	}

	@Test
	public void testisValidSupervisorFailure() throws Exception {
		regOsiDto.setOfficerId(null);
		authResponseDTO.setStatus(false);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());
	}

	@Test
	public void testisValidOSIFailurewithRetry() throws Exception {
		authResponseDTO.setStatus(false);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setRetryCount(1);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());
	}

	@Test
	public void testException() throws Exception {

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(null);

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertEquals(true, messageDto.getInternalError());

	}

}
