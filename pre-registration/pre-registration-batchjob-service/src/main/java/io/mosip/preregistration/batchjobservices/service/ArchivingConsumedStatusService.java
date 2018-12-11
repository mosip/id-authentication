package io.mosip.preregistration.batchjobservices.service;

import org.springframework.stereotype.Service;

import io.mosip.preregistration.batchjobservices.dto.ResponseDto;

/**
 * @author M1043008
 *
 */
/**
 * This service is used to archive the consumed PreId from 
 * Applicant demographic to history table
 *
 */
@Service
public interface ArchivingConsumedStatusService {

	/**
	 * archive consumed PreId
	 * 
	 * @return the response dto
	 */
	ResponseDto<String> archivingConsumed();

}