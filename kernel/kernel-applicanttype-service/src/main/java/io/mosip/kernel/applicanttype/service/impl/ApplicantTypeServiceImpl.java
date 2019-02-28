package io.mosip.kernel.applicanttype.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.applicanttype.constant.ApplicantTypeErrorCode;
import io.mosip.kernel.applicanttype.dto.KeyValues;
import io.mosip.kernel.applicanttype.dto.request.RequestDTO;
import io.mosip.kernel.applicanttype.dto.response.ApplicantTypeCodeDTO;
import io.mosip.kernel.applicanttype.dto.response.ResponseDTO;
import io.mosip.kernel.applicanttype.exception.DataNotFoundException;
import io.mosip.kernel.applicanttype.exception.RequestException;
import io.mosip.kernel.applicanttype.service.ApplicantTypeService;
import io.mosip.kernel.core.applicanttype.exception.InvalidApplicantArgumentException;
import io.mosip.kernel.core.applicanttype.spi.ApplicantType;

@Service
public class ApplicantTypeServiceImpl implements ApplicantTypeService {

	@Autowired
	private ApplicantType applicantCodeService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.applicanttype.service.ApplicantTypeService#getApplicantType(
	 * io.mosip.kernel.applicanttype.dto.RequestDTO)
	 */
	@Override
	public ResponseDTO getApplicantType(RequestDTO dto) {
		KeyValues keyValues = dto.getRequest();
		Map<String, String> map = keyValues.getRequest();
		ResponseDTO response = new ResponseDTO();
		ApplicantTypeCodeDTO appDto = new ApplicantTypeCodeDTO();
		try {
			appDto.setApplicationtypecode(applicantCodeService.getApplicantType(map));
		} catch (InvalidApplicantArgumentException e) {
			throw new RequestException(ApplicantTypeErrorCode.INVALID_REQUEST_EXCEPTION.getErrorCode(),
					ApplicantTypeErrorCode.INVALID_REQUEST_EXCEPTION.getErrorMessage());
		}
		if (appDto.getApplicationtypecode() == null || appDto.getApplicationtypecode().trim().length() == 0) {
			throw new DataNotFoundException(ApplicantTypeErrorCode.NO_APPLICANT_FOUND_EXCEPTION.getErrorCode(),
					ApplicantTypeErrorCode.NO_APPLICANT_FOUND_EXCEPTION.getErrorMessage());
		}
		response.setResponse(appDto);
		response.setId(dto.getId());
		response.setVer(dto.getVer());
		response.setTimestamp(dto.getTimestamp());
		return response;
	}

}
