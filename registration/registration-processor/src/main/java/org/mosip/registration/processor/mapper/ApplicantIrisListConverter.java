package org.mosip.registration.processor.mapper;

import java.util.LinkedList;
import java.util.List;

import org.mosip.registration.processor.dto.biometric.IrisDetailsDTO;
import org.mosip.registration.processor.dto.json.metadata.Iris;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * class is Applicant iris List Converter
 * 
 * @author YASWANTH S
 *
 */
public class ApplicantIrisListConverter extends CustomConverter<List<IrisDetailsDTO>, List<Iris>> {

	
	@Override
	public List<Iris> convert(List<IrisDetailsDTO> source, Type<? extends List<Iris>> destinationType) {
		LinkedList<Iris> irisList = new LinkedList<Iris>();
		source.forEach((irisDetailsDTO) -> {

			Iris iris = new Iris();
			// iris.setIrisImageName(irisDetailsDTO.getIrisName());
			iris.setQualityScore(irisDetailsDTO.getQualityScore());
			iris.setForceCaptured(irisDetailsDTO.isForceCaptured());
			iris.setIrisType(irisDetailsDTO.getIrisType());
			irisList.add(iris);

		});
		return irisList;
	}

	
}
