package io.mosip.kernel.applicanttype.service.impl;

import java.util.HashMap;
import java.util.List;
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
		ResponseDTO response = new ResponseDTO();

		List<KeyValues<String, Object>> list = dto.getAttributes();
		Map<String, Object> map = new HashMap<>();
		for (KeyValues<String, Object> keyValues : list) {
			map.put(keyValues.getAttribute(), keyValues.getValue());
		}

		ApplicantTypeCodeDTO appDto = new ApplicantTypeCodeDTO();
		try {
			appDto.setApplicantTypeCode(applicantCodeService.getApplicantType(map));
		} catch (InvalidApplicantArgumentException e) {
			throw new RequestException(ApplicantTypeErrorCode.INVALID_REQUEST_EXCEPTION.getErrorCode(),
					ApplicantTypeErrorCode.INVALID_REQUEST_EXCEPTION.getErrorMessage(), e);
		}
		if (appDto.getApplicantTypeCode() == null || appDto.getApplicantTypeCode().trim().length() == 0) {
			throw new DataNotFoundException(ApplicantTypeErrorCode.NO_APPLICANT_FOUND_EXCEPTION.getErrorCode(),
					ApplicantTypeErrorCode.NO_APPLICANT_FOUND_EXCEPTION.getErrorMessage());
		}
		response.setApplicantType(appDto);
		return response;
	}

}