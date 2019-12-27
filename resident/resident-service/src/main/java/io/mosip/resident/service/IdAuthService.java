package io.mosip.resident.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.resident.constant.AuthTypeStatus;
import io.mosip.resident.dto.AuthTxnDetailsDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.OtpValidationFailedException;

@Service
public interface IdAuthService {

	public boolean validateOtp(String transactionID, String individualId, String individualIdType, String otp)
			throws OtpValidationFailedException;

	public boolean authTypeStatusUpdate(String individualId, String individualIdType, List<String> authType,
			AuthTypeStatus authTypeStatus) throws ApisResourceAccessException;
	
	public List<AuthTxnDetailsDTO> getAuthHistoryDetails(String individualId, String individualIdType,
			Integer pageStart,Integer pageFetch) throws ApisResourceAccessException;
}
