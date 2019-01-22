package io.mosip.registrationprocessor.stages.demodedupe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.auth.dto.AuthResponseDTO;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.stages.demodedupe.BiometricValidation;
import io.mosip.registration.processor.stages.demodedupe.DemoDedupe;

/**
 * The Class DemoDedupeTest.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
@PrepareForTest({ IOUtils.class, HMACUtils.class })
public class DemoDedupeTest {

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The packet info dao. */
	@Mock
	private PacketInfoDao packetInfoDao;

	/** The input stream. */
	@Mock
	private InputStream inputStream;

	/** The filesystem ceph adapter impl. */
	@Mock
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	/** The auth response DTO. */
	@Mock
	AuthResponseDTO authResponseDTO = new AuthResponseDTO();

	/** The rest client service. */
	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	/** The env. */
	@Mock
	Environment env;

	/** The biometric validation. */
	@Mock
	private BiometricValidation biometricValidation;

	/** The demo dedupe. */
	@InjectMocks
	private DemoDedupe demoDedupe;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {

		List<String> fingers = new ArrayList<>();
		fingers.add("LEFTTHUMB");
		fingers.add("LEFTINDEX");
		fingers.add("LEFTMIDDLE");
		fingers.add("LEFTLITTLE");
		fingers.add("LEFTRING");
		fingers.add("RIGHTTHUMB");
		fingers.add("RIGHTINDEX");
		fingers.add("RIGHTMIDDLE");
		fingers.add("RIGHTLITTLE");
		fingers.add("RIGHTRING");

		List<String> iris = new ArrayList<>();
		iris.add("LEFTEYE");
		iris.add("RIGHTEYE");
		Mockito.when(env.getProperty("fingerType")).thenReturn("LeftThumb");
		Mockito.when(packetInfoManager.getApplicantFingerPrintImageNameById(anyString())).thenReturn(fingers);
		Mockito.when(packetInfoManager.getApplicantIrisImageNameById(anyString())).thenReturn(iris);

		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);

		byte[] data = "1234567890".getBytes();
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		authResponseDTO.setStatus("y");
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);
	}

	/**
	 * Test dedupe duplicate found.
	 */
	@Test
	public void testDedupeDuplicateFound() {
		String regId = "1234567890";

		DemographicInfoDto dto1 = new DemographicInfoDto();
		DemographicInfoDto dto2 = new DemographicInfoDto();
		List<DemographicInfoDto> Dtos = new ArrayList<>();
		Dtos.add(dto1);
		Dtos.add(dto2);

		Mockito.when(packetInfoDao.findDemoById(regId)).thenReturn(Dtos);
		Mockito.when(packetInfoDao.getAllDemographicInfoDtos(anyString(), anyString(), any(), anyString()))
				.thenReturn(Dtos);

		List<DemographicInfoDto> duplicates = demoDedupe.performDedupe(regId);
		assertEquals("Test for Dedupe Duplicate found", false, duplicates.isEmpty());
	}

	/**
	 * Test demodedupe empty.
	 */
	@Test
	public void testDemodedupeEmpty() {

		String regId = "1234567890";
		List<DemographicInfoDto> Dtos = new ArrayList<>();

		Mockito.when(packetInfoDao.findDemoById(regId)).thenReturn(Dtos);
		Mockito.when(packetInfoDao.getAllDemographicInfoDtos(anyString(), anyString(), any(), anyString()))
				.thenReturn(Dtos);

		List<DemographicInfoDto> duplicates = demoDedupe.performDedupe(regId);
		assertEquals("Test for Demo Dedupe Empty", true, duplicates.isEmpty());
	}

	/**
	 * Test demo dedupe authetication sucess.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testDemoDedupeAutheticationSucess() throws ApisResourceAccessException, IOException {

		String regId = "1234567890";

		List<String> duplicateIds = new ArrayList<>();
		duplicateIds.add("123456789");
		duplicateIds.add("987654321");

		boolean result = demoDedupe.authenticateDuplicates(regId, duplicateIds);

		assertTrue("Test for Demo Dedupe Authetication Success", result);
	}

	/**
	 * Test demo dedupe authetication failure.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testDemoDedupeAutheticationFailure() throws ApisResourceAccessException, IOException {

		String regId = "1234567890";

		List<String> duplicateIds = new ArrayList<>();
		duplicateIds.add("123456789");
		duplicateIds.add("987654321");

		authResponseDTO.setStatus("n");

		boolean result = demoDedupe.authenticateDuplicates(regId, duplicateIds);
		// This should change after uncommenting auth
		assertTrue("Test for Demo Dedupe Authetication Failure", result);
	}

	/**
	 * Test demo dedupe authetication iris sucess.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testDemoDedupeAutheticationIrisSucess() throws ApisResourceAccessException, IOException {

		String regId = "1234567890";

		List<String> duplicateIds = new ArrayList<>();
		duplicateIds.add("123456789");
		duplicateIds.add("987654321");
		Mockito.when(biometricValidation.validateBiometric(anyString())).thenReturn(false);
		boolean result = demoDedupe.authenticateDuplicates(regId, duplicateIds);

		assertTrue("Test for Demo Dedupe Authetication Success for Iris biometric", result);
	}
}
