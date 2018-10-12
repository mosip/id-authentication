package io.mosip.registration.test.mapper;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.json.metadata.OSIData;
import io.mosip.registration.mapper.CustomObjectMapper;
import ma.glasnost.orika.MapperFacade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class OSIDataConvertorTest {

	private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;
	private DemographicDTO demographicDTO;
	private OSIDataDTO osiDataDTO;

	@Before
	public void initialize() {
		demographicDTO = new DemographicDTO();
		demographicDTO.setIntroducerUIN("2017654245456");

		osiDataDTO = new OSIDataDTO();
		osiDataDTO.setIntroducerType("Parent");
		osiDataDTO.setIntroducerName("Will Smith");
		osiDataDTO.setOperatorID("hw28je28");
		osiDataDTO.setSupervisorID("wej223uu8o");
		osiDataDTO.setSupervisorName("Jack Rob");
	}

	@Test
	public void testOSIDataConvertor() {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setDemographicDTO(demographicDTO);

		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setOperatorBiometricDTO(getFingerprint());
		biometricDTO.setSupervisorBiometricDTO(getFingerprint());
		biometricDTO.setIntroducerBiometricDTO(getFingerprint());
		registrationDTO.setBiometricDTO(biometricDTO);

		registrationDTO.setOsiDataDTO(osiDataDTO);

		OSIData osiData = mapperFacade.convert(registrationDTO, OSIData.class, "osiDataConverter");

		validateResult(registrationDTO, osiData);
		assertNotNull(osiData.getIntroducerUINHash());
		assertNull(osiData.getIntroducerRIDHash());
		assertEquals(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO().getFingerprintDetailsDTO().get(0)
				.getFingerprintImageName(), osiData.getIntroducerFingerprintImage());
		assertEquals(registrationDTO.getBiometricDTO().getSupervisorBiometricDTO().getFingerprintDetailsDTO().get(0)
				.getFingerprintImageName(), osiData.getSupervisorFingerprintImage());
		assertEquals(registrationDTO.getBiometricDTO().getOperatorBiometricDTO().getFingerprintDetailsDTO().get(0)
				.getFingerprintImageName(), osiData.getOperatorFingerprintImage());
	}

	@Test
	public void testIrisImage() {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		demographicDTO.setIntroducerRID("2017654212345150623");
		registrationDTO.setDemographicDTO(demographicDTO);

		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setOperatorBiometricDTO(getIris());
		biometricDTO.setSupervisorBiometricDTO(getIris());
		biometricDTO.setIntroducerBiometricDTO(getIris());
		registrationDTO.setBiometricDTO(biometricDTO);

		registrationDTO.setOsiDataDTO(osiDataDTO);

		OSIData osiData = mapperFacade.convert(registrationDTO, OSIData.class, "osiDataConverter");

		validateResult(registrationDTO, osiData);
		assertNotNull(osiData.getIntroducerRIDHash());
		assertNull(osiData.getIntroducerUINHash());
		assertEquals(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO().getIrisDetailsDTO().get(0)
				.getIrisImageName(), osiData.getIntroducerIrisImage());
		assertEquals(registrationDTO.getBiometricDTO().getSupervisorBiometricDTO().getIrisDetailsDTO().get(0)
				.getIrisImageName(), osiData.getSupervisorIrisName());
		assertEquals(registrationDTO.getBiometricDTO().getOperatorBiometricDTO().getIrisDetailsDTO().get(0)
				.getIrisImageName(), osiData.getOperatorIrisName());
	}

	@Test
	public void testNoIntroducer() {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setDemographicDTO(new DemographicDTO());
		registrationDTO.setBiometricDTO(new BiometricDTO());

		OSIDataDTO osiDataDTO = new OSIDataDTO();
		osiDataDTO.setOperatorID("hw28je28");
		osiDataDTO.setSupervisorID("wej223uu8o");
		osiDataDTO.setSupervisorName("Jack Rob");
		
		registrationDTO.setOsiDataDTO(osiDataDTO);

		OSIData osiData = mapperFacade.convert(registrationDTO, OSIData.class, "osiDataConverter");

		validateResult(registrationDTO, osiData);
		assertNull(osiData.getIntroducerRIDHash());
		assertNull(osiData.getIntroducerUINHash());
		assertNull(osiData.getIntroducerIrisImage());
		assertNull(osiData.getSupervisorIrisName());
		assertNull(osiData.getOperatorIrisName());
		assertNull(osiData.getIntroducerFingerprintImage());
		assertNull(osiData.getSupervisorFingerprintImage());
		assertNull(osiData.getOperatorFingerprintImage());
	}
	
	private void validateResult(RegistrationDTO registrationDTO, OSIData osiData) {
		assertEquals(registrationDTO.getOsiDataDTO().getIntroducerType(), osiData.getIntroducerType());
		assertEquals(registrationDTO.getDemographicDTO().getIntroducerUIN(), osiData.getIntroducerUIN());
		assertEquals(registrationDTO.getDemographicDTO().getIntroducerRID(), osiData.getIntroducerRID());
		// assertEquals(registrationDTO.getOsiDataDTO().getIntroducerName(),
		// osiData.getIntroducerName());
		assertEquals(registrationDTO.getOsiDataDTO().getOperatorID(), osiData.getOperatorId());
		assertEquals(registrationDTO.getOsiDataDTO().getSupervisorID(), osiData.getSupervisorId());
		assertEquals(registrationDTO.getOsiDataDTO().getSupervisorName(), osiData.getSupervisorName());
	}

	private BiometricInfoDTO getFingerprint() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		List<FingerprintDetailsDTO> fingerprints = new ArrayList<>();

		FingerprintDetailsDTO fingerPrint = new FingerprintDetailsDTO();
		fingerPrint.setFingerprintImageName("LeftThumb");
		fingerPrint.setFingerType("LeftThumb");
		fingerprints.add(fingerPrint);

		biometricInfoDTO.setFingerprintDetailsDTO(fingerprints);

		return biometricInfoDTO;
	}

	private BiometricInfoDTO getIris() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		List<IrisDetailsDTO> ires = new ArrayList<>();

		IrisDetailsDTO iris = new IrisDetailsDTO();
		iris.setIrisImageName("LeftEye");
		iris.setIrisType("LeftEye");
		ires.add(iris);

		biometricInfoDTO.setIrisDetailsDTO(ires);

		return biometricInfoDTO;
	}

}
