package io.mosip.registration.mapper;

import java.util.List;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.json.metadata.OSIData;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * class is OSI data Converter
 * 
 * @author YASWANTH S
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class OSIDataConverter extends CustomConverter<RegistrationDTO, OSIData> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.glasnost.orika.Converter#convert(java.lang.Object,
	 * ma.glasnost.orika.metadata.Type)
	 */
	@Override
	public OSIData convert(RegistrationDTO source, Type<? extends OSIData> destinationType) {
		BiometricDTO biometricDTO = source.getBiometricDTO();
		BiometricInfoDTO biometricInfoDTO = null;
		OSIData osiData = new OSIData();

		// Add Operator Details
		osiData.setOperatorId(source.getOsiDataDTO().getOperatorID());
		biometricInfoDTO = biometricDTO.getOperatorBiometricDTO();
		if (biometricInfoDTO != null) {
			osiData.setOperatorFingerprintImage(getImageName(biometricInfoDTO.getFingerprintDetailsDTO()));
			osiData.setOperatorIrisName(getImageName(biometricInfoDTO.getIrisDetailsDTO()));
		}

		// Add Supervisor Details
		osiData.setSupervisorId(source.getOsiDataDTO().getSupervisorID());
		osiData.setSupervisorName(source.getOsiDataDTO().getSupervisorName());
		biometricInfoDTO = biometricDTO.getSupervisorBiometricDTO();
		if (biometricInfoDTO != null) {
			osiData.setSupervisorFingerprintImage(getImageName(biometricInfoDTO.getFingerprintDetailsDTO()));
			osiData.setSupervisorIrisName(getImageName(biometricInfoDTO.getIrisDetailsDTO()));
		}

		// Introducer
		OSIDataDTO osiDataDTO = source.getOsiDataDTO();
		if (osiDataDTO.getIntroducerType() != null) {
			osiData.setIntroducerType(osiDataDTO.getIntroducerType());
			osiData.setIntroducerName(osiDataDTO.getIntroducerName());
			
			String introducerId = source.getDemographicDTO().getIntroducerUIN();
			if (introducerId != null) {
				osiData.setIntroducerUIN(introducerId);
				osiData.setIntroducerUINHash(HMACUtils.digestAsPlainText(HMACUtils.generateHash(introducerId.getBytes())));
			}
			
			introducerId = source.getDemographicDTO().getIntroducerRID();
			if (introducerId != null) {
				osiData.setIntroducerRID(introducerId);
				osiData.setIntroducerRIDHash(HMACUtils.digestAsPlainText(HMACUtils.generateHash(introducerId.getBytes())));
			}
			
			biometricInfoDTO = biometricDTO.getIntroducerBiometricDTO();
			if (biometricInfoDTO != null) {
				osiData.setIntroducerFingerprintImage(getImageName(biometricInfoDTO.getFingerprintDetailsDTO()));
				osiData.setIntroducerIrisImage(getImageName(biometricInfoDTO.getIrisDetailsDTO()));
			}
		}

		return osiData;
	}

	private boolean checkNotEmpty(List<?> list) {
		return list != null && !list.isEmpty();
	}

	private String getImageName(List<?> baseDTOs) {
		String imageName = null;
		if (checkNotEmpty(baseDTOs)) {
			if (baseDTOs.get(0) instanceof FingerprintDetailsDTO) {
				imageName = ((FingerprintDetailsDTO) baseDTOs.get(0)).getFingerprintImageName();
			} else if (baseDTOs.get(0) instanceof IrisDetailsDTO) {
				imageName = ((IrisDetailsDTO) baseDTOs.get(0)).getIrisImageName();
			}
		}
		return imageName;
	}

}
