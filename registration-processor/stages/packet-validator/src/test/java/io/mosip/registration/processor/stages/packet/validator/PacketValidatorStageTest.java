package io.mosip.registration.processor.stages.packet.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonUtil.class, IOUtils.class, HMACUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class PacketValidatorStageTest {

	@Mock
	private InputStream inputStream;

	@Mock
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@InjectMocks
	private PacketValidatorStage packetValidatorStage = new PacketValidatorStage() {
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
		packetValidatorStage.deployVerticle();
	}

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();

	private PacketMetaInfo packetMetaInfo;

	// private Demographic demographicinfo;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		packetMetaInfo = new PacketMetaInfo();
		Identity identity = new Identity();

		List<FieldValueArray> fieldValueArrayList = new ArrayList<FieldValueArray>();

		FieldValueArray applicantBiometric = new FieldValueArray();
		applicantBiometric.setLabel(PacketFiles.APPLICANTBIOMETRICSEQUENCE.name());
		List<String> applicantBiometricValues = new ArrayList<String>();
		applicantBiometricValues.add(PacketFiles.BOTHTHUMBS.name());
		applicantBiometric.setValue(applicantBiometricValues);
		fieldValueArrayList.add(applicantBiometric);
		FieldValueArray introducerBiometric = new FieldValueArray();
		introducerBiometric.setLabel(PacketFiles.INTRODUCERBIOMETRICSEQUENCE.name());
		List<String> introducerBiometricValues = new ArrayList<String>();
		introducerBiometricValues.add("introducerLeftThumb");
		introducerBiometric.setValue(introducerBiometricValues);
		fieldValueArrayList.add(introducerBiometric);
		FieldValueArray applicantDemographic = new FieldValueArray();
		applicantDemographic.setLabel(PacketFiles.APPLICANTDEMOGRAPHICSEQUENCE.name());
		List<String> applicantDemographicValues = new ArrayList<String>();
		applicantDemographicValues.add(PacketFiles.DEMOGRAPHICINFO.name());
		applicantDemographicValues.add(PacketFiles.APPLICANTPHOTO.name());
		applicantDemographic.setValue(applicantDemographicValues);
		fieldValueArrayList.add(applicantDemographic);
		identity.setHashSequence(fieldValueArrayList);
		packetMetaInfo.setIdentity(identity);
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_405.toString(), EventName.UPDATE.toString(),
				EventType.BUSINESS.toString(), "1234testcase");

	}

	@Test
	public void testStructuralValidationSuccess() throws Exception {
		String test = "1234567890";
		byte[] data = "1234567890".getBytes();

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		MessageDTO dto = new MessageDTO();
		dto.setRid("2018701130000410092018110735");

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.doNothing().when(HMACUtils.class, "update", data);
		PowerMockito.when(HMACUtils.class, "digestAsPlainText", anyString().getBytes()).thenReturn(test);

		Mockito.doNothing().when(packetInfoManager).savePacketData(packetMetaInfo.getIdentity());
		Mockito.doNothing().when(packetInfoManager).saveDemographicInfoJson(inputStream,
				packetMetaInfo.getIdentity().getMetaData());

		MessageDTO messageDto = packetValidatorStage.process(dto);

		assertTrue(messageDto.getIsValid());

	}

	@Test
	public void testCheckSumValidationFailure() throws Exception {
		String test = "123456789";
		byte[] data = "1234567890".getBytes();

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		MessageDTO dto = new MessageDTO();
		dto.setRid("2018701130000410092018110735");

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.doNothing().when(HMACUtils.class, "update", data);
		PowerMockito.when(HMACUtils.class, "digestAsPlainText", anyString().getBytes()).thenReturn(test);

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());

	}

	@Test
	public void testFilesValidationFailure() throws Exception {

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		MessageDTO dto = new MessageDTO();
		dto.setRid("2018701130000410092018110735");

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.FALSE);

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());
	}

	@Test
	public void testExceptions() throws Exception {

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		packetMetaInfo.getIdentity().setHashSequence(null);

		MessageDTO dto = new MessageDTO();
		dto.setRid("2018701130000410092018110735");

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		MessageDTO messageDto = packetValidatorStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	@Test
	public void testIOExceptions() throws Exception {

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenThrow(new IOException());

		MessageDTO dto = new MessageDTO();
		dto.setRid("2018701130000410092018110735");

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		MessageDTO messageDto = packetValidatorStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	@Test
	public void testCheckSumValidationFailureWithRetryCount() throws Exception {
		String test = "123456789";
		byte[] data = "1234567890".getBytes();

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		MessageDTO dto = new MessageDTO();
		dto.setRid("2018701130000410092018110735");

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setRetryCount(1);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.doNothing().when(HMACUtils.class, "update", data);
		PowerMockito.when(HMACUtils.class, "digestAsPlainText", anyString().getBytes()).thenReturn(test);

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());

	}

}