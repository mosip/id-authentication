package io.mosip.registration.service.sync;

import io.mosip.registration.dto.ResponseDTO;

/**
 * {@code RegistrationApprovalService} is the registration approval service Interface
 *
 * @author Mahesh Kumar
 */
public interface SyncStatusValidatorService {

	/**
	 * {@code validateSyncStatus} class is to validate all the conditions referring
	 * to sync.
	 *
	 * @return String Error messages
	 */
	public ResponseDTO validateSyncStatus();
	
}
