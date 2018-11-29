package io.mosip.registration.service.sync;

import java.sql.Timestamp;

import io.mosip.registration.dto.ResponseDTO;

public interface PreRegistrationDataSyncService {

	public ResponseDTO getPreRegistrationIds();
	
	public ResponseDTO getPreRegistration(String preRegistrationId);
}
