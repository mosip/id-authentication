package io.mosip.kernel.applicanttype.service;

import io.mosip.kernel.applicanttype.dto.request.RequestDTO;
import io.mosip.kernel.applicanttype.dto.response.ResponseDTO;

public interface ApplicantTypeService {

	/**
	 * This method return the applicant id.
	 * 
	 * @param dto Request dto
	 * @return applicant id
	 */
	public ResponseDTO getApplicantType(RequestDTO dto);

}
