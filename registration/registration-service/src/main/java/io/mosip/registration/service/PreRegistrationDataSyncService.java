package io.mosip.registration.service;

import java.sql.Timestamp;

import io.mosip.registration.dto.ResponseDTO;

public interface PreRegistrationDataSyncService {

	public ResponseDTO getPreRegistrationIds(String id, Timestamp reqTime, Timestamp fromDate, Timestamp toDate,
			String regClientId, String userId, String ver);
	
	public ResponseDTO getPreRegistration(String preRegistrationId);
}
