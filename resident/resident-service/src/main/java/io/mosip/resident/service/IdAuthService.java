package io.mosip.resident.service;

import org.springframework.stereotype.Service;

@Service
public interface IdAuthService {

    public boolean validateOtp(String transactionID, String individualId, String individualIdType, String otp);
}
