package io.mosip.registration.processor.bio.dedupe.service.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.util.ReflectionTestUtils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.registration.processor.bio.dedupe.exception.ABISAbortException;
import io.mosip.registration.processor.bio.dedupe.exception.ABISInternalError;
import io.mosip.registration.processor.bio.dedupe.exception.UnableToServeRequestABISException;
import io.mosip.registration.processor.bio.dedupe.exception.UnexceptedError;
import io.mosip.registration.processor.bio.dedupe.service.impl.BioDedupeServiceImpl;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.CandidateListDto;
import io.mosip.registration.processor.core.packet.dto.abis.CandidatesDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class BioDedupeServiceImplTest.
 */
@RefreshScope
@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOUtils.class, JsonUtil.class })
public class BioDedupeServiceImplTest {

	/** The input stream. */
	@Mock
	InputStream inputStream;

	/** The rest client service. */
	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	/** The packet info manager. */
	@Mock
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The abis insert responce dto. */
	@Mock
	AbisInsertResponseDto abisInsertResponseDto = new AbisInsertResponseDto();

	/** The bio dedupe service. */
	@InjectMocks
	BioDedupeServiceImpl bioDedupeService = new BioDedupeServiceImpl();

	/** The identify response. */
	private AbisIdentifyResponseDto identifyResponse = new AbisIdentifyResponseDto();

	/** The registration id. */
	String registrationId = "1000";

	/** The adapter. */
	@Mock
	PacketManager adapter;

	private PacketMetaInfo packetMetaInfo;

	/** The identity. */
	Identity identity = new Identity();

	private ListAppender<ILoggingEvent> listAppender;

	private Logger fooLogger;
	
	@Mock
	private RegistrationStatusService registrationStatusService;

	/**
	 * Setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {

		Mockito.doNothing().when(packetInfoManager).saveAbisRef(any());

		abisInsertResponseDto.setReturnValue(2);

		ReflectionTestUtils.setField(bioDedupeService, "maxResults", 30);
		ReflectionTestUtils.setField(bioDedupeService, "targetFPIR", 30);
		ReflectionTestUtils.setField(bioDedupeService, "threshold", 60);

		String refId = "01234567-89AB-CDEF-0123-456789ABCDEF";
		List<String> refIdList = new ArrayList<>();
		refIdList.add(refId);
		Mockito.when(packetInfoManager.getReferenceIdByRid(anyString())).thenReturn(refIdList);

		CandidatesDto candidate1 = new CandidatesDto();
		candidate1.setReferenceId("01234567-89AB-CDEF-0123-456789ABCDEG");
		candidate1.setScaledScore("70");

		CandidatesDto candidate2 = new CandidatesDto();
		candidate2.setReferenceId("01234567-89AB-CDEF-0123-456789ABCDEH");
		candidate2.setScaledScore("80");

		CandidatesDto[] candidateArray = new CandidatesDto[2];
		candidateArray[0] = candidate1;
		candidateArray[1] = candidate2;

		CandidateListDto candidateList = new CandidateListDto();
		candidateList.setCandidates(candidateArray);

		identifyResponse.setCandidateList(candidateList);

		List<DemographicInfoDto> demoList = new ArrayList<>();
		DemographicInfoDto demo1 = new DemographicInfoDto();
		demoList.add(demo1);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demoList);
		Mockito.when(registrationStatusService.checkUinAvailabilityForRid(anyString())).thenReturn(true);
		packetMetaInfo = new PacketMetaInfo();
		List<FieldValueArray> fieldValueArrayList = new ArrayList<FieldValueArray>();
		FieldValueArray introducerBiometric = new FieldValueArray();
		introducerBiometric.setLabel(PacketFiles.APPLICANTBIOMETRICSEQUENCE.name());
		List<String> introducerBiometricValues = new ArrayList<String>();
		introducerBiometricValues.add("applicant_bio_CBEFF");
		introducerBiometric.setValue(introducerBiometricValues);
		fieldValueArrayList.add(introducerBiometric);
		identity.setHashSequence(fieldValueArrayList);
		packetMetaInfo.setIdentity(identity);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		fooLogger = (Logger) LoggerFactory.getLogger(BioDedupeServiceImpl.class);
		listAppender = new ListAppender<>();

	}

	/**
	 * Insert biometrics success test.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test
	public void insertBiometricsSuccessTest() throws ApisResourceAccessException, IOException {

		abisInsertResponseDto.setReturnValue(1);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponseDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("success"));

	}

	/**
	 * Insert biometrics ABIS internal error failure test.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test(expected = ABISInternalError.class)
	public void insertBiometricsABISInternalErrorFailureTest() throws ApisResourceAccessException, IOException {

		abisInsertResponseDto.setFailureReason(1);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponseDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("2"));

	}

	/**
	 * Insert biometrics ABIS abort exception failure test.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test(expected = ABISAbortException.class)
	public void insertBiometricsABISAbortExceptionFailureTest() throws ApisResourceAccessException, IOException {

		abisInsertResponseDto.setFailureReason(2);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponseDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("2"));

	}

	/**
	 * Insert biometrics unexcepted error failure test.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test(expected = UnexceptedError.class)
	public void insertBiometricsUnexceptedErrorFailureTest() throws ApisResourceAccessException, IOException {

		abisInsertResponseDto.setFailureReason(3);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponseDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("2"));

	}

	/**
	 * Insert biometrics unable to serve request ABIS exception failure test.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test(expected = UnableToServeRequestABISException.class)
	public void insertBiometricsUnableToServeRequestABISExceptionFailureTest() throws ApisResourceAccessException, IOException {

		abisInsertResponseDto.setFailureReason(4);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponseDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("2"));

	}

	/**
	 * Test perform dedupe success.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test
	public void testPerformDedupeSuccess() throws ApisResourceAccessException, IOException {

		identifyResponse.setReturnValue(1);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(identifyResponse);
		String rid = "27847657360002520181208094056";

		List<String> list = new ArrayList<>();
		list.add(rid);
		Mockito.when(packetInfoManager.getRidByReferenceId(anyString())).thenReturn(list);

		List<String> ridList = new ArrayList<>();
		ridList.add(rid);
		ridList.add(rid);

		List<DemographicInfoDto> demoList = new ArrayList<>();
		DemographicInfoDto demo1 = new DemographicInfoDto();
		demoList.add(demo1);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demoList);

		List<String> duplicates = bioDedupeService.performDedupe(rid);

		assertEquals(ridList, duplicates);
	}

	/**
	 * Test perform dedupe failure.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test(expected = ABISInternalError.class)
	public void testPerformDedupeFailure() throws ApisResourceAccessException, IOException {

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(identifyResponse);
		String rid = "27847657360002520181208094056";
		identifyResponse.setReturnValue(2);
		identifyResponse.setFailureReason(1);

		bioDedupeService.performDedupe(rid);
	}

	/**
	 * Test dedupe abis abort exception.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test(expected = ABISAbortException.class)
	public void testDedupeAbisAbortException() throws ApisResourceAccessException, IOException {

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(identifyResponse);
		String rid = "27847657360002520181208094056";
		identifyResponse.setReturnValue(2);
		identifyResponse.setFailureReason(2);

		bioDedupeService.performDedupe(rid);
	}

	/**
	 * Test dedupe unexpected error.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test(expected = UnexceptedError.class)
	public void testDedupeUnexpectedError() throws ApisResourceAccessException, IOException {

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(identifyResponse);
		String rid = "27847657360002520181208094056";
		identifyResponse.setReturnValue(2);
		identifyResponse.setFailureReason(3);

		bioDedupeService.performDedupe(rid);
	}

	/**
	 * Test dedupe unable to serve request ABIS exception.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException 
	 */
	@Test(expected = UnableToServeRequestABISException.class)
	public void testDedupeUnableToServeRequestABISException() throws ApisResourceAccessException, IOException {

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(identifyResponse);
		String rid = "27847657360002520181208094056";
		identifyResponse.setReturnValue(2);
		identifyResponse.setFailureReason(4);

		bioDedupeService.performDedupe(rid);
	}

	/**
	 * Test get file.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testGetFile() throws Exception {
		byte[] data = "1234567890".getBytes();
		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		byte[] fileData = bioDedupeService.getFile(registrationId);
		assertArrayEquals(fileData, data);
	}

	@Test
	public void IOExceptionTest() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		byte[] data = "1234567890".getBytes();
		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenThrow(new IOException());

		byte[] fileData = bioDedupeService.getFile(registrationId);
		Assertions.assertThatExceptionOfType(IOException.class);
	}
}
