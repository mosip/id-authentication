/*package io.mosip.registration.processor.stages.packet.validator;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.packet.dto.BiometricSequence;
import io.mosip.registration.processor.core.packet.dto.DemographicSequence;
import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonUtils.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class PacketValidatorStageTest {

	@Mock
	private InputStream inputStream;

	@Mock
	private Environment env;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private FileSystemAdapter<InputStream, PacketFiles, Boolean> filesystemCephAdapterImpl = new FilesystemCephAdapterImpl();

	@Mock
	RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	;

	@InjectMocks
	private PacketValidatorStage packetValidatorStage;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void filesValidationCheck() throws Exception {
		PacketInfo packetInfo = new PacketInfo();
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
		demographicApplicantList.add(PacketFiles.REGISTRATIONACKNOWLDEGEMENT.name());
		demographicApplicantList.add(PacketFiles.APPLICANTPHOTO.name());
		demographicApplicantList.add(PacketFiles.PROOFOFADDRESS.name());

		biometricSequence.setApplicant(biometricApplicantList);
		biometricSequence.setIntroducer(biometricIntroducerList);

		demographicSequence.setApplicant(demographicApplicantList);
		hashSequence.setBiometricSequence(biometricSequence);
		hashSequence.setDemographicSequence(demographicSequence);
		packetInfo.setHashSequence(hashSequence);
		String PACKET_META_INFO_WITH_EXTENSION = "PacketMetaInfo.json";

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		when(env.getProperty("VALIDATION")).thenReturn("test");
		Mockito.doNothing().when(fileManager).put(PACKET_META_INFO_WITH_EXTENSION, inputStream,
				DirectoryPathDto.VALIDATION);

		PowerMockito.mockStatic(JsonUtils.class);
		PowerMockito.when(JsonUtils.class, "jsonFileToJavaObject", PacketInfo.class,
				"test" + File.separator + "PacketMetaInfo.json").thenReturn(packetInfo);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);
		MessageDTO dto = new MessageDTO();
		dto.setRid("1234");

		RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());

	}

}*/