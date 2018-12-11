package io.mosip.preregistration.batchjobservices.service;

import org.springframework.stereotype.Service;

import io.mosip.preregistration.batchjobservices.dto.ResponseDto;

/**
 * @author M1043008
 *
 */
@Service
public interface ArchivingConsumedStatusService {

	ResponseDto<String> archivingConsumed();

}