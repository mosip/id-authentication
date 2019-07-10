package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.impl.UserOnboardDAOImpl;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.entity.CenterMachine;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.id.CenterMachineId;
import io.mosip.registration.entity.id.RegMachineSpecId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.UserBiometricRepository;
import io.mosip.registration.repositories.UserMachineMappingRepository;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SessionContext.class, ApplicationContext.class })
public class UserOnBoardDAOImlpTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private UserMachineMappingRepository userMachineMappingRepository;

	@Mock
	private UserBiometricRepository userBiometricRepository;

	@Mock
	private CenterMachineRepository centerMachineRepository;

	@Mock
	private MachineMasterRepository machineMasterRepository;

	@InjectMocks
	private UserOnboardDAOImpl userOnboardDAOImpl;

	@Before
	public void initialize() throws Exception {
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getUserId()).thenReturn("mosip");
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.when(ApplicationContext.applicationLanguage()).thenReturn("eng");

		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.USER_STATION_ID, "1947");
		appMap.put(RegistrationConstants.USER_CENTER_ID, "1947");
		PowerMockito.when(ApplicationContext.map()).thenReturn(appMap);

	}

	@Test
	public void UserOnBoardSuccess() throws IOException {

		List<UserBiometric> bioMetricsList = new ArrayList<>();

		BiometricDTO biometricDTO = new BiometricDTO();

		List<FingerprintDetailsDTO> listOfFingerPrints = new ArrayList<>();
		List<FingerprintDetailsDTO> listOfFingerSegmets = new ArrayList<>();

		File file = new File(URLDecoder.decode(ClassLoader.getSystemResource("ISOTemplate.iso").getFile(), "UTF-8"));
		byte[] data = FileUtils.readFileToByteArray(file);

		FingerprintDetailsDTO fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint(data);
		fingerDto.setFingerprintImageName("leftIndex");
		fingerDto.setFingerPrintISOImage("leftIndex".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint(data);
		fingerDto.setFingerprintImageName("leftLittle");
		fingerDto.setFingerPrintISOImage("leftLittle".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint(data);
		fingerDto.setFingerprintImageName("leftMiddle");
		fingerDto.setFingerPrintISOImage("leftMiddle".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint(data);
		fingerDto.setFingerprintImageName("leftRing");
		fingerDto.setFingerPrintISOImage("leftRing".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint(data);
		fingerDto.setFingerprintImageName("leftThumb");
		fingerDto.setFingerPrintISOImage("leftThumb".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint(data);
		fingerDto.setFingerprintImageName("leftFore");
		fingerDto.setFingerPrintISOImage("leftFore".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		List<IrisDetailsDTO> iriesList = new ArrayList<>();
		IrisDetailsDTO iries = new IrisDetailsDTO();

		iries.setIris("right".getBytes());
		iries.setIrisImageName("Right");
		iries.setIrisType("Eyes");
		iries.setNumOfIrisRetry(2);
		iries.setQualityScore(90);
		iriesList.add(iries);

		FaceDetailsDTO face = new FaceDetailsDTO();
		face.setFace("face".getBytes());
		face.setForceCaptured(false);
		face.setNumOfRetries(2);
		face.setQualityScore(90);

		BiometricInfoDTO info = new BiometricInfoDTO();
		info.setFingerprintDetailsDTO(listOfFingerPrints);
		info.setIrisDetailsDTO(iriesList);
		info.setFace(face);

		biometricDTO.setOperatorBiometricDTO(info);

		Mockito.when(userBiometricRepository.saveAll(bioMetricsList)).thenReturn(bioMetricsList);
		doNothing().when(userBiometricRepository).deleteByUserBiometricIdUsrId(Mockito.anyString());

		assertNotNull(userOnboardDAOImpl.insert(biometricDTO));

	}

	@Test
	public void savetest() {
		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(userMachineMappingRepository.save(Mockito.any(UserMachineMapping.class)))
				.thenReturn(machineMapping);
		userOnboardDAOImpl.save();
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RuntimeException.class)
	public void saveFailuretest() {

		UserMachineMapping machineMapping = new UserMachineMapping();
		Mockito.when(userMachineMappingRepository.save(Mockito.any(UserMachineMapping.class)))
				.thenThrow(RuntimeException.class);
		userMachineMappingRepository.save(machineMapping);
	}

	@SuppressWarnings("serial")
	@Test
	public void UserOnBoardException() {

		BiometricDTO biometricDTO = new BiometricDTO();

		List<FingerprintDetailsDTO> listOfFingerPrints = new ArrayList<>();
		List<FingerprintDetailsDTO> listOfFingerSegmets = new ArrayList<>();

		FingerprintDetailsDTO fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint("leftIndex".getBytes());
		fingerDto.setFingerprintImageName("leftIndex");
		fingerDto.setFingerPrintISOImage("leftIndex".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint("leftLittle".getBytes());
		fingerDto.setFingerprintImageName("leftLittle");
		fingerDto.setFingerPrintISOImage("leftLittle".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint("leftMiddle".getBytes());
		fingerDto.setFingerprintImageName("leftMiddle");
		fingerDto.setFingerPrintISOImage("leftMiddle".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint("leftRing".getBytes());
		fingerDto.setFingerprintImageName("leftRing");
		fingerDto.setFingerPrintISOImage("leftRing".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint("leftThumb".getBytes());
		fingerDto.setFingerprintImageName("leftThumb");
		fingerDto.setFingerPrintISOImage("leftThumb".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		fingerDto = new FingerprintDetailsDTO();
		fingerDto.setFingerPrint("leftFore".getBytes());
		fingerDto.setFingerprintImageName("leftFore");
		fingerDto.setFingerPrintISOImage("leftFore".getBytes());
		fingerDto.setFingerType("FingerPrint");
		fingerDto.setNumRetry(2);
		fingerDto.setQualityScore(90);
		listOfFingerSegmets.add(fingerDto);
		fingerDto.setSegmentedFingerprints(listOfFingerSegmets);
		listOfFingerPrints.add(fingerDto);

		List<IrisDetailsDTO> iriesList = new ArrayList<>();
		IrisDetailsDTO iries = new IrisDetailsDTO();

		iries.setIris("right".getBytes());
		iries.setIrisImageName("Right");
		iries.setIrisType("Eyes");
		iries.setNumOfIrisRetry(2);
		iries.setQualityScore(90);
		iriesList.add(iries);

		FaceDetailsDTO face = new FaceDetailsDTO();
		face.setFace("face".getBytes());
		face.setForceCaptured(false);
		face.setNumOfRetries(2);
		face.setQualityScore(90);

		BiometricInfoDTO info = new BiometricInfoDTO();
		info.setFingerprintDetailsDTO(listOfFingerPrints);
		info.setIrisDetailsDTO(iriesList);
		info.setFace(face);

		biometricDTO.setOperatorBiometricDTO(info);

		try {
			Mockito.when(userBiometricRepository.saveAll(anyObject())).thenThrow(new RuntimeException("...") {
			});
			assertNotNull(userOnboardDAOImpl.insert(biometricDTO));
		} catch (Exception e) {

		}

	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getStationIDRunException() throws RegBaseCheckedException {
		Mockito.when(machineMasterRepository
				.findByIsActiveTrueAndMacAddressAndRegMachineSpecIdLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new RegBaseUncheckedException());
		userOnboardDAOImpl.getStationID("8C-16-45-88-E7-0B");
	}

	@Test
	public void getStationID() throws RegBaseCheckedException {
		MachineMaster machineMaster = new MachineMaster();
		machineMaster.setMacAddress("8C-16-45-88-E7-0C");
		RegMachineSpecId regMachineSpecId = new RegMachineSpecId();
		regMachineSpecId.setId("100311");
		regMachineSpecId.setLangCode("eng");
		machineMaster.setRegMachineSpecId(regMachineSpecId);
		Mockito.when(machineMasterRepository
				.findByIsActiveTrueAndMacAddressAndRegMachineSpecIdLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(machineMaster);
		String stationId = userOnboardDAOImpl.getStationID("8C-16-45-88-E7-0C");
		Assert.assertSame("100311", stationId);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getCenterIDRunExceptionTest() throws RegBaseCheckedException {
		Mockito.when(centerMachineRepository.findByIsActiveTrueAndCenterMachineIdId(Mockito.anyString()))
				.thenThrow(new RegBaseUncheckedException());
		userOnboardDAOImpl.getCenterID("StationID1947");
	}

	@Test
	public void getCenterID() throws RegBaseCheckedException {
		CenterMachineId centerMachineId = new CenterMachineId();
		centerMachineId.setId("StationID1947");
		centerMachineId.setCentreId("CenterID1947");

		CenterMachine centerMachine = new CenterMachine();
		centerMachine.setCenterMachineId(centerMachineId);

		Mockito.when(centerMachineRepository.findByIsActiveTrueAndCenterMachineIdId(Mockito.anyString()))
				.thenReturn(centerMachine);
		String stationId = userOnboardDAOImpl.getCenterID("StationID1947");
		Assert.assertSame("CenterID1947", stationId);
	}

	@Test
	public void getLastUpdatedTime() {
		UserMachineMapping userMachineMapping = new UserMachineMapping();		
		userMachineMapping.setCrDtime(new Timestamp(System.currentTimeMillis()));		
		Mockito.when(userMachineMappingRepository.findByUserMachineMappingIdUserID(Mockito.anyString())).thenReturn(userMachineMapping);
		Assert.assertNotNull(userOnboardDAOImpl.getLastUpdatedTime("Usr123"));
	}
	
	@Test(expected = RuntimeException.class)
	public void getLastUpdatedTimeFailure() {		
		Mockito.when(userMachineMappingRepository.findByUserMachineMappingIdUserID(Mockito.anyString())).thenThrow(RuntimeException.class);
		Assert.assertNotNull(userOnboardDAOImpl.getLastUpdatedTime("Usr123"));
	}

}
