package org.mosip.registration.mapper;

import java.util.LinkedList;
import java.util.List;

import org.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import org.mosip.registration.dto.json.metadata.Fingerprints;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/** class is Applicant finger print List Converter
 * @author YASWANTH S
 *
 */
public class ApplicantFingerPrintListConverter
		extends CustomConverter<List<FingerprintDetailsDTO>, List<Fingerprints>> {

	


	@Override
	public List<Fingerprints> convert(List<FingerprintDetailsDTO> source,
			Type<? extends List<Fingerprints>> destinationType) {
		LinkedList<Fingerprints> fingerPrintList = new LinkedList<Fingerprints>();
		source.forEach((fingerprintDetailsDTO) -> {

			Fingerprints fingerprints=new Fingerprints();
			fingerprints.setFingerPrintName(fingerprintDetailsDTO.getFingerPrintName());
			fingerprints.setQualityScore(fingerprintDetailsDTO.getQualityScore());
			fingerprints.setForceCaptured(fingerprintDetailsDTO.isForceCaptured());
			fingerprints.setFingerType(fingerprintDetailsDTO.getFingerType());
			fingerPrintList.add(fingerprints);
			
		});
		return fingerPrintList;
	}

	
}
