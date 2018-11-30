package io.mosip.registration.service.sync;

import io.mosip.registration.dto.ResponseDTO;

public interface PreRegistrationDataSyncService {

	public ResponseDTO getPreRegistrationIds();
	
	public byte[] getPreRegistration(String preRegistrationId);
}
