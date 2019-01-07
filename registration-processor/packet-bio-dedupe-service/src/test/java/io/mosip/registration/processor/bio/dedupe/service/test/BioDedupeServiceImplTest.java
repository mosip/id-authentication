package io.mosip.registration.processor.bio.dedupe.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.processor.abis.dto.AbisInsertResponceDto;
import io.mosip.registration.processor.abis.dto.CandidateListDto;
import io.mosip.registration.processor.abis.dto.CandidatesDto;
import io.mosip.registration.processor.abis.dto.IdentityResponceDto;
import io.mosip.registration.processor.bio.dedupe.exception.ABISAbortException;
import io.mosip.registration.processor.bio.dedupe.exception.ABISInternalError;
import io.mosip.registration.processor.bio.dedupe.exception.UnableToServeRequestABISException;
import io.mosip.registration.processor.bio.dedupe.exception.UnexceptedError;
import io.mosip.registration.processor.bio.dedupe.service.impl.BioDedupeServiceImpl;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

@RefreshScope
@RunWith(PowerMockRunner.class)
public class BioDedupeServiceImplTest {

	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	@Mock
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Mock
	AbisInsertResponceDto abisInsertResponceDto = new AbisInsertResponceDto();

	@InjectMocks
	BioDedupeServiceImpl bioDedupeService = new BioDedupeServiceImpl();

	private IdentityResponceDto identifyResponse = new IdentityResponceDto();

	String registrationId = "1000";

	@Before
	public void setup() throws ApisResourceAccessException {
		Mockito.doNothing().when(packetInfoManager).saveAbisRef(any());

		abisInsertResponceDto.setReturnValue("2");

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
		demo1.setUin("123456789");
		demoList.add(demo1);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demoList);

	}

	@Test
	public void insertBiometricsSuccessTest() throws ApisResourceAccessException {

		abisInsertResponceDto.setReturnValue("1");
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponceDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("success"));

	}

	@Test(expected = ABISInternalError.class)
	public void insertBiometricsABISInternalErrorFailureTest() throws ApisResourceAccessException {

		abisInsertResponceDto.setFailureReason(1);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponceDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("2"));

	}

	@Test(expected = ABISAbortException.class)
	public void insertBiometricsABISAbortExceptionFailureTest() throws ApisResourceAccessException {

		abisInsertResponceDto.setFailureReason(2);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponceDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("2"));

	}

	@Test(expected = UnexceptedError.class)
	public void insertBiometricsUnexceptedErrorFailureTest() throws ApisResourceAccessException {

		abisInsertResponceDto.setFailureReason(3);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponceDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("2"));

	}

	@Test(expected = UnableToServeRequestABISException.class)
	public void insertBiometricsUnableToServeRequestABISExceptionFailureTest() throws ApisResourceAccessException {

		abisInsertResponceDto.setFailureReason(4);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponceDto);

		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("2"));

	}

	@Test
	public void testPerformDedupeSuccess() throws ApisResourceAccessException {

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
		demo1.setUin("123456789");
		demoList.add(demo1);
		Mockito.when(packetInfoManager.findDemoById(anyString())).thenReturn(demoList);

		List<String> duplicates = bioDedupeService.performDedupe(rid);

		assertEquals(ridList, duplicates);
	}

	@Test(expected = ABISInternalError.class)
	public void testPerformDedupeFailure() throws ApisResourceAccessException {

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(identifyResponse);
		String rid = "27847657360002520181208094056";
		identifyResponse.setReturnValue(2);
		identifyResponse.setFailureReason(1);

		bioDedupeService.performDedupe(rid);
	}

	@Test(expected = ABISAbortException.class)
	public void testDedupeAbisAbortException() throws ApisResourceAccessException {

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(identifyResponse);
		String rid = "27847657360002520181208094056";
		identifyResponse.setReturnValue(2);
		identifyResponse.setFailureReason(2);

		bioDedupeService.performDedupe(rid);
	}

	@Test(expected = UnexceptedError.class)
	public void testDedupeUnexpectedError() throws ApisResourceAccessException {

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(identifyResponse);
		String rid = "27847657360002520181208094056";
		identifyResponse.setReturnValue(2);
		identifyResponse.setFailureReason(3);

		bioDedupeService.performDedupe(rid);
	}

	@Test(expected = UnableToServeRequestABISException.class)
	public void testDedupeUnableToServeRequestABISException() throws ApisResourceAccessException {

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(identifyResponse);
		String rid = "27847657360002520181208094056";
		identifyResponse.setReturnValue(2);
		identifyResponse.setFailureReason(4);

		bioDedupeService.performDedupe(rid);
	}

}
