package org.mosip.registration.processor.mapper;

import java.util.LinkedList;
import java.util.List;

import org.mosip.registration.processor.dto.demographic.DocumentDetailsDTO;
import org.mosip.registration.processor.dto.json.metadata.DocumentDetails;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/** class is Applicant Document List Converter
 * @author YASWANTH S
 *
 */
public class ApplicantDocumentListConverter
		extends CustomConverter<List<DocumentDetailsDTO>, List<DocumentDetails>> {

	


	/* (non-Javadoc)
	 * @see ma.glasnost.orika.Converter#convert(java.lang.Object, ma.glasnost.orika.metadata.Type)
	 */
	@Override
	public List<DocumentDetails> convert(List<DocumentDetailsDTO> source,
			Type<? extends List<DocumentDetails>> destinationType) {
		LinkedList<DocumentDetails> documentDetailsList = new LinkedList<DocumentDetails>();
		source.forEach((documentDetailsDTO) -> {

			DocumentDetails documentDetails = new DocumentDetails();
			documentDetails.setDocumentCategory(documentDetailsDTO.getDocumentCategory());
			documentDetails.setDocumentOwner(documentDetailsDTO.getDocumentOwner());
			documentDetails.setDocumentType(documentDetailsDTO.getDocumentType());
			documentDetails.setDocumentName(documentDetailsDTO.getDocumentName());
			documentDetailsList.add(documentDetails);

		});
		return documentDetailsList;
	}
}
