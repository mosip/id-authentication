package io.mosip.registration.mapper;

import java.util.LinkedList;
import java.util.List;

import io.mosip.registration.dto.biometric.ExceptionFingerprintDetailsDTO;
import io.mosip.registration.dto.json.metadata.ExceptionFingerprints;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * class is Applicant exception finger List Converter
 * 
 * @author YASWANTH S
 *
 */
public class ApplicantExceptionFingerPrintListConverter
		extends CustomConverter<List<ExceptionFingerprintDetailsDTO>, List<ExceptionFingerprints>> {



	/* (non-Javadoc)
	 * @see ma.glasnost.orika.Converter#convert(java.lang.Object, ma.glasnost.orika.metadata.Type)
	 */
	@Override
	public List<ExceptionFingerprints> convert(List<ExceptionFingerprintDetailsDTO> source,
			Type<? extends List<ExceptionFingerprints>> destinationType) {
		LinkedList<ExceptionFingerprints> exceptionFingerPrintList = new LinkedList<ExceptionFingerprints>();
		source.forEach((exceptionFingerprintDetailsDTO) -> {

			ExceptionFingerprints exceptionFingerprints = new ExceptionFingerprints();
			exceptionFingerprints.setMissingFinger(exceptionFingerprintDetailsDTO.getMissingFinger());
			exceptionFingerprints.setExceptionDescription(exceptionFingerprintDetailsDTO.getExceptionDescription());
			exceptionFingerprints.setExceptionType(exceptionFingerprintDetailsDTO.getExceptionType());
			exceptionFingerPrintList.add(exceptionFingerprints);

		});
		return exceptionFingerPrintList;
	}
}
