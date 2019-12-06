package io.mosip.resident.service;

import java.util.List;

import io.mosip.resident.exception.OtpValidationFailedException;
import org.springframework.stereotype.Service;

import io.mosip.resident.exception.ApisResourceAccessException;

@Service
public interface IdAuthService {

	public boolean validateOtp(String transactionID, String individualId, String individualIdType, String otp) throws OtpValidationFailedException;

	public boolean authTypeStatusUpdate(String individualId, String individualIdType, List<String> authType,
			boolean isLock) throws ApisResourceAccessException;
}
