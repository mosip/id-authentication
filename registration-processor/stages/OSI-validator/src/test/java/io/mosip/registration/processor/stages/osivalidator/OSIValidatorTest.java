/**
 * 
 */
package io.mosip.registration.processor.stages.osivalidator;

import static org.junit.Assert.assertFalse;
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

import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.TransactionService;

/**
 * The Class OSIValidatorTest.
 *
 * @author M1022006
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class OSIValidatorTest {

	/** The input stream. */
	@Mock
	private InputStream inputStream;

	/** The packet info manager. */
	@Mock
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The adapter. */
	@Mock
	FileSystemAdapter<InputStream, Boolean> adapter;

	/** The rest client service. */
	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	/** The transcation status service. */
	@Mock
	private TransactionService<TransactionDto> transcationStatusService;

	/** The auth response DTO. */
	@Mock
	AuthResponseDTO authResponseDTO = new AuthResponseDTO();

	/** The data. */
	byte[] data = "1234567890".getBytes();

	/** The reg osi dto. */
	private RegOsiDto regOsiDto = new RegOsiDto();

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	/** The transaction dto. */
	TransactionDto transactionDto = new TransactionDto();

	/** The osi validator. */
	@InjectMocks
	OSIValidator osiValidator;

	List<DemographicInfoDto> demographicDedupeDtoList = new ArrayList<>();

	DemographicInfoDto demographicInfoDto = new DemographicInfoDto();

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		demographicInfoDto.setUin("1234");
		osiValidator.registrationStatusDto = registrationStatusDto;
		regOsiDto.setOfficerId("O1234");
		regOsiDto.setOfficerFingerpImageName("fingerprint");
		regOsiDto.setOfficerfingerType("RIGHTLITTLE");
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

		regOsiDto.setIntroducerUin(null);
		regOsiDto.setIntroducerRegId("reg1234");
		regOsiDto.setIntroducerTyp("Parent");
		regOsiDto.setIntroducerFingerpImageName("introducerFingerpImageName");
		regOsiDto.setIntroducerFingerpType("RIGHTRING");
		regOsiDto.setIntroducerIrisImageName("IntroducerIrisImageName");
		regOsiDto.setIntroducerPhotoName("IntroducerPhotoName");
		regOsiDto.setIntroducerIrisType("RIGHTEYE");
		registrationStatusDto.setApplicantType("Child");
		demographicDedupeDtoList.add(demographicInfoDto);

		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		Mockito.when(adapter.checkFileExistence(anyString(), anyString())).thenReturn(true);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		authResponseDTO.setStatus("y");
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		registrationStatusDto.setRegistrationId("reg1234");
		registrationStatusDto.setApplicantType("Child");
		registrationStatusDto.setRegistrationType("New");

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
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertTrue(isValid);

	}

	/**
	 * Test officer details null.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testOfficerDetailsNull() throws Exception {
		regOsiDto.setOfficerFingerpImageName(null);
		regOsiDto.setOfficerIrisImageName(null);
		regOsiDto.setOfficerPhotoName(null);
		regOsiDto.setOfficerHashedPin(null);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Test introducer details null.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testIntroducerDetailsNull() throws Exception {
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);
		regOsiDto.setOfficerfingerType("LEFTMIDDLE");
		regOsiDto.setSupervisorFingerType("RIGHTINDEX");
		regOsiDto.setIntroducerFingerpImageName(null);
		regOsiDto.setIntroducerIrisImageName(null);
		regOsiDto.setIntroducerPhotoName(null);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Testis valid OSI failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testisValidOSIFailure() throws Exception {
		authResponseDTO.setStatus("N");
		regOsiDto.setOfficerfingerType("LEFTLITTLE");
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Test supervisor details null.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testSupervisorDetailsNull() throws Exception {
		regOsiDto.setOfficerfingerType("RIGHTTHUMB");

		regOsiDto.setSupervisorFingerpImageName(null);
		regOsiDto.setSupervisorIrisImageName(null);
		regOsiDto.setSupervisorPhotoName(null);
		regOsiDto.setSupervisorHashedPin(null);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Test invalid iris.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testInvalidIris() throws Exception {
		authResponseDTO.setStatus("N");
		regOsiDto.setOfficerId(null);
		regOsiDto.setSupervisorFingerpImageName(null);
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	@Test
	public void testIntroducerUIN() throws Exception {
		regOsiDto.setIntroducerRegId(null);
		regOsiDto.setIntroducerUin(null);

		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertFalse(isValid);
	}

	/**
	 * Test invalid iris.
	 * 
	 * @throws IOException
	 * @throws ApisResourceAccessException
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void tesAllIntroducerFingerPrint() throws ApisResourceAccessException, IOException {
		regOsiDto.setIntroducerFingerpType("LEFTTHUMB");
		regOsiDto.setOfficerfingerType("RIGHTMIDDLE");
		regOsiDto.setSupervisorFingerType("LEFTRING");
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsiDto);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demographicDedupeDtoList);
		Mockito.when(transcationStatusService.getTransactionByRegIdAndStatusCode(anyString(), anyString()))
				.thenReturn(transactionDto);

		boolean isValid = osiValidator.isValidOSI("reg1234");

		assertTrue(isValid);
	}
}
