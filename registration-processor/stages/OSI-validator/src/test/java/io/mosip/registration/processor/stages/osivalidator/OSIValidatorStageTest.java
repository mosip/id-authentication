package io.mosip.registration.processor.stages.osivalidator;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
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
	private OSIValidatorStage osiValidatorStage;

	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	private Identity identity;

	@Before
	public void setUp() throws Exception {
		identity = new Identity();

		List<FieldValue> fieldValueosi = new ArrayList<FieldValue>();

		FieldValue biometric = new FieldValue();
		biometric.setLabel(PacketFiles.OFFICERID.name());
		biometric.setValue("1234");
		biometric.setLabel(PacketFiles.OFFICERFINGERPRINTIMAGE.name());
		biometric.setValue("fingerprint");
		biometric.setLabel(PacketFiles.OFFICERIRISIMAGE.name());
		biometric.setValue("iris");
		biometric.setLabel(PacketFiles.OFFICERAUTHENTICATIONIMAGE.name());
		biometric.setValue("auth");
		biometric.setLabel(PacketFiles.OFFICERPIN.name());
		biometric.setValue("1234");

		fieldValueosi.add(biometric);
		identity.setOsiData(fieldValueosi);

		List<FieldValue> fieldValuemeta = new ArrayList<FieldValue>();
		FieldValue metadatavalue = new FieldValue();
		metadatavalue.setLabel(PacketFiles.OFFICERFINGERPRINTTYPE.name());
		metadatavalue.setValue("lefttumb");
		metadatavalue.setLabel(PacketFiles.OFFICERIRISTYPE.name());
		metadatavalue.setValue("lefteye");

		fieldValuemeta.add(metadatavalue);
		identity.setMetaData(fieldValuemeta);

	}

	@Test
	public void testisValidOSI() throws Exception {

		Mockito.when(adapter.getFile(any(), any())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, Identity.class).thenReturn(identity);

		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		Mockito.when(adapter.checkFileExistence(anyString(), anyString())).thenReturn(true);

		// Mockito.when(osiValidatorStage.osiValidator.validateBiometric(any(), any(),
		// any(), any())).thenReturn(true);

		MessageDTO dto = new MessageDTO();
		dto.setRid("2018701130000410092018110735");
		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setRegistrationId("2018701130000410092018110735");

		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);

		MessageDTO messageDto = osiValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());
		// osiValidatorStage.osiValidator.isValidOSI("1234");
	}
}
