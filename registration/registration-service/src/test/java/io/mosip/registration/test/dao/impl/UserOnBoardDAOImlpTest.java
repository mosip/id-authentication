package io.mosip.registration.test.dao.impl;

import static org.mockito.Matchers.anyObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dao.impl.UserOnboardDAOImpl;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.entity.CenterMachine;
import io.mosip.registration.entity.CenterMachineId;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.UserBiometricRepository;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Ignore
public class UserOnBoardDAOImlpTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private MachineMappingDAO machineMappingDAO;
	
	@Mock
	private UserBiometricRepository userBiometricRepository;
	
	@Mock
	private CenterMachineRepository centerMachineRepository;

	@Mock
	private MachineMasterRepository machineMasterRepository;
	
	@InjectMocks
	private UserOnboardDAOImpl userOnboardDAOImpl;
	
	@BeforeClass
	public static void beforeClass() throws URISyntaxException {
		
		SessionContext.getInstance().setMapObject(new HashMap<String, Object>());
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.USER_STATION_ID, "1947");
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterId("abc123");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(centerDetailDTO);

	}

	@Test
	public void UserOnBoardSuccess() throws IOException {

		List<UserBiometric> bioMetricsList = new ArrayList<>();

		BiometricDTO biometricDTO = new BiometricDTO();

		List<FingerprintDetailsDTO> listOfFingerPrints = new ArrayList<>();
		List<FingerprintDetailsDTO> listOfFingerSegmets = new ArrayList<>();
		
		
		File file = new File(ClassLoader.getSystemResource("ISOTemplate.iso").getFile());
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
		info.setFaceDetailsDTO(face);

		biometricDTO.setOperatorBiometricDTO(info);
		
		UserMachineMapping user = new UserMachineMapping();

		Mockito.when(userBiometricRepository.saveAll(bioMetricsList)).thenReturn(bioMetricsList);
		Mockito.when(machineMappingDAO.save(user)).thenReturn("success");
		
		userOnboardDAOImpl.insert(biometricDTO);
		

	}
	
	@SuppressWarnings("serial")
	@Test
	public void UserOnBoardException() {

		List<UserBiometric> bioMetricsList = new ArrayList<>();

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
		info.setFaceDetailsDTO(face);

		biometricDTO.setOperatorBiometricDTO(info);

		try {
			Mockito.when(userBiometricRepository.saveAll(anyObject())).thenThrow(new RuntimeException("...") {
			});
			userOnboardDAOImpl.insert(biometricDTO);
		}catch(Exception e) {
			
		}

	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void getStationIDRunException() throws RegBaseCheckedException {
		Mockito.when(machineMasterRepository.findByMacAddress(Mockito.anyString()))
				.thenThrow(new RegBaseUncheckedException());
		userOnboardDAOImpl.getStationID("8C-16-45-88-E7-0B");
	}

	@Test
	public void getStationID() throws RegBaseCheckedException {
		MachineMaster machineMaster = new MachineMaster();
		machineMaster.setMacAddress("8C-16-45-88-E7-0C");
		machineMaster.setId("StationID1947");
		Mockito.when(machineMasterRepository.findByMacAddress(Mockito.anyString())).thenReturn(machineMaster);
		String stationId = userOnboardDAOImpl.getStationID("8C-16-45-88-E7-0C");
		Assert.assertSame("StationID1947", stationId);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getCenterIDRunExceptionTest() throws RegBaseCheckedException {
		Mockito.when(centerMachineRepository.findByCenterMachineIdId(Mockito.anyString()))
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

		Mockito.when(centerMachineRepository.findByCenterMachineIdId(Mockito.anyString())).thenReturn(centerMachine);
		String stationId = userOnboardDAOImpl.getCenterID("StationID1947");
		Assert.assertSame("CenterID1947", stationId);
	}

	
}
