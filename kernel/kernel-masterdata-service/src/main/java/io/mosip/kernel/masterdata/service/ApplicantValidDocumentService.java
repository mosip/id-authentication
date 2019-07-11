package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.ApplicantValidDocumentDto;

public interface ApplicantValidDocumentService {

	public ApplicantValidDocumentDto getDocumentCategoryAndTypes(String applicantTypeCode, List<String> langCodes);

}
