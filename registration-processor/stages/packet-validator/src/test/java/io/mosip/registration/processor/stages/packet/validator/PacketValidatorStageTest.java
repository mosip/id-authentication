package io.mosip.registration.processor.stages.packet.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;

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
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.packet.dto.BiometricSequence;
import io.mosip.registration.processor.core.packet.dto.Demographic;
import io.mosip.registration.processor.core.packet.dto.DemographicSequence;
import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
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
	private FileSystemAdapter<InputStream, Boolean> filesystemCephAdapterImpl = new FilesystemCephAdapterImpl();

	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	PacketInfoManager<PacketInfo, Demographic, MetaData> packetinfomanager;

	@InjectMocks
	private PacketValidatorStage packetValidatorStage;

	private PacketInfo packetInfo;

	private Demographic demographicinfo;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		packetInfo = new PacketInfo();
		HashSequence hashSequence = new HashSequence();
		BiometricSequence biometricSequence = new BiometricSequence();
		DemographicSequence demographicSequence = new DemographicSequence();
		List<String> biometricApplicantList = new ArrayList<String>();

		biometricApplicantList.add(PacketFiles.LEFTPALM.name());
		biometricApplicantList.add(PacketFiles.RIGHTPALM.name());
		biometricApplicantList.add(PacketFiles.LEFTEYE.name());
		biometricApplicantList.add(PacketFiles.RIGHTEYE.name());
		biometricApplicantList.add(PacketFiles.BOTHTHUMBS.name());

		List<String> biometricIntroducerList = new ArrayList<String>();
		biometricIntroducerList.add(PacketFiles.LEFTTHUMB.name());
		biometricIntroducerList.add(PacketFiles.RIGHTTHUMB.name());
		biometricIntroducerList.add(PacketFiles.LEFTEYE.name());
		biometricIntroducerList.add(PacketFiles.RIGHTEYE.name());

		List<String> demographicApplicantList = new ArrayList<String>();
		demographicApplicantList.add(PacketFiles.DEMOGRAPHICINFO.name());
		demographicApplicantList.add(PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name());
		demographicApplicantList.add(PacketFiles.APPLICANTPHOTO.name());
		demographicApplicantList.add(PacketFiles.PROOFOFADDRESS.name());
		demographicApplicantList.add(PacketFiles.EXCEPTIONPHOTO.name());

		biometricSequence.setApplicant(biometricApplicantList);
		biometricSequence.setIntroducer(biometricIntroducerList);

		demographicSequence.setApplicant(demographicApplicantList);
		hashSequence.setBiometricSequence(biometricSequence);
		hashSequence.setDemographicSequence(demographicSequence);
		packetInfo.setHashSequence(hashSequence);
	}

	@Test
	public void testStructuralValidationSuccess() throws Exception {
		String test = "1234567890";
		byte[] data = "1234567890".getBytes();

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketInfo.class)
				.thenReturn(packetInfo);

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

		Mockito.doNothing().when(packetinfomanager).savePacketData(packetInfo);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, Demographic.class)
				.thenReturn(demographicinfo);
		Mockito.doNothing().when(packetinfomanager).saveDemographicData(demographicinfo, packetInfo.getMetaData());

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());

	}

	@Test
	public void testCheckSumValidationFailure() throws Exception {
		String test = "123456789";
		byte[] data = "1234567890".getBytes();

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketInfo.class)
				.thenReturn(packetInfo);

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

		Mockito.doNothing().when(packetinfomanager).savePacketData(packetInfo);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, Demographic.class)
				.thenReturn(demographicinfo);
		Mockito.doNothing().when(packetinfomanager).saveDemographicData(demographicinfo, packetInfo.getMetaData());

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());

	}

	@Test
	public void testFilesValidationFailure() throws Exception {

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketInfo.class)
				.thenReturn(packetInfo);

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
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketInfo.class)
				.thenReturn(packetInfo);

		packetInfo.setHashSequence(null);

		MessageDTO dto = new MessageDTO();
		dto.setRid("2018701130000410092018110735");

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		MessageDTO messageDto = packetValidatorStage.process(dto);

		assertEquals(true, messageDto.getInternalError());
	}

}