package io.mosip.registration.service.sync;

import io.mosip.registration.dto.ResponseDTO;

public interface PreRegistrationDataSyncService {

	public ResponseDTO getPreRegistrationIds(String syncJobId);
	
	public ResponseDTO getPreRegistration(String preRegistrationId);
}
