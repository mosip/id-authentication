package org.mosip.registration.processor.mapper;

import org.mosip.registration.processor.dto.PacketDTO;
import org.mosip.registration.processor.dto.json.metadata.OSIData;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * class is osi data List Converter
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public class OSIDataConverter extends CustomConverter<PacketDTO, OSIData> {

	

	/* (non-Javadoc)
	 * @see ma.glasnost.orika.Converter#convert(java.lang.Object, ma.glasnost.orika.metadata.Type)
	 */
	@Override
	public OSIData convert(PacketDTO source, Type<? extends OSIData> destinationType) {
		String introducerType = source.getOsiDataDTO().getIntroducerType();
		OSIData osiData = new OSIData();
		osiData.setOperatorUIN(source.getOsiDataDTO().getOperatorUIN());
		osiData.setOperatorName(source.getOsiDataDTO().getOperatorName());

		if (source.getBiometricDTO().getOperatorBiometricDTO() != null) {
			osiData.setOperatorFingerprintName(source.getBiometricDTO().getOperatorBiometricDTO()
					.getFingerprintDetailsDTO().get(0).getFingerType());

		}
		if (source.getBiometricDTO().getOperatorBiometricDTO() != null) {
			osiData.setOperatorIrisName(
					source.getBiometricDTO().getOperatorBiometricDTO().getIrisDetailsDTO().get(0).getIrisType());
		}
		osiData.setSupervisorUIN(source.getOsiDataDTO().getSupervisorUIN());
		osiData.setSupervisorName(source.getOsiDataDTO().getSupervisorName());
		if (source.getBiometricDTO().getSupervisorBiometricDTO() != null) {
			osiData.setSupervisorFingerprintName(source.getBiometricDTO().getSupervisorBiometricDTO()
					.getFingerprintDetailsDTO().get(0).getFingerType());
			osiData.setSupervisorIrisName(
					source.getBiometricDTO().getSupervisorBiometricDTO().getIrisDetailsDTO().get(0).getIrisType());
		}

		// Introducer
		if (introducerType.equalsIgnoreCase("introducer")) {
			osiData.setIntroducerUIN(source.getDemographicDTO().getIntroducerUIN());
			if (source.getBiometricDTO().getIntroducerBiometricDTO() != null) {
				osiData.setIntroducerFingerprintName(source.getBiometricDTO().getIntroducerBiometricDTO()
						.getFingerprintDetailsDTO().get(0).getFingerType());

				osiData.setIntroducerIrisName(
						source.getBiometricDTO().getIntroducerBiometricDTO().getIrisDetailsDTO().get(0).getIrisType());
			}
		}

		// HOF
		if (introducerType.equalsIgnoreCase("hof")) {
			osiData.setIntroducerUIN(source.getDemographicDTO().getHofUIN());
			if (source.getBiometricDTO().getHofBiometricDTO() != null) {
				osiData.setIntroducerFingerprintName(source.getBiometricDTO().getHofBiometricDTO()
						.getFingerprintDetailsDTO().get(0).getFingerType());
				osiData.setIntroducerIrisName(
						source.getBiometricDTO().getHofBiometricDTO().getIrisDetailsDTO().get(0).getIrisType());
			}

		}

		return osiData;
	}

}
