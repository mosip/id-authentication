package org.mosip.registration.mapper;

import java.util.LinkedList;
import java.util.List;

import org.mosip.registration.dto.biometric.ExceptionIrisDetailsDTO;
import org.mosip.registration.dto.json.metadata.ExceptionIris;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/** class is Applicant exception iris List Converter
 * @author YASWANTH S
 *
 */
public class ApplicantExceptionIrisConverter
		extends CustomConverter<List<ExceptionIrisDetailsDTO>, List<ExceptionIris>> {

	
	

	/* (non-Javadoc)
	 * @see ma.glasnost.orika.Converter#convert(java.lang.Object, ma.glasnost.orika.metadata.Type)
	 */
	@Override
	public List<ExceptionIris> convert(List<ExceptionIrisDetailsDTO> source,
			Type<? extends List<ExceptionIris>> destinationType) {
		LinkedList<ExceptionIris> exceptionIrisList = new LinkedList<ExceptionIris>();
		source.forEach((exceptionIrisDetailsDTO) -> {

			ExceptionIris exceptionIris=new ExceptionIris();
			exceptionIris.setMissingIris(exceptionIrisDetailsDTO.getMissingIris());
			exceptionIris.setExceptionDescription(exceptionIrisDetailsDTO.getExceptionDescription());
			exceptionIrisList.add(exceptionIris);
			
		});
		return exceptionIrisList;
	}

	

}
