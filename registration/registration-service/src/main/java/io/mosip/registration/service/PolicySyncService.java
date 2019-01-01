package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

/**
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface PolicySyncService {
	ResponseDTO fetchPolicy(String centerId);

	/*ResponseDTO updatePolicy(PolicyDTO policyDTO);*/

}
