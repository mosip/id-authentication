package io.mosip.preregistration.batchjobservices.service;

import org.springframework.stereotype.Service;

import io.mosip.preregistration.batchjobservices.dto.ResponseDto;


/**
 * @author M1043008
 *
 */
/**
 * This service is used to update the status of applicant demographic table
 * from Preocessed PreId List table..
 *
 */
@Service
public interface BatchJobService {

	/**
	 * Update status of Applicant Demographic.
	 * @return the response dto
	 */
	ResponseDto<String> demographicConsumedStatus();

}