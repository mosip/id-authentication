package io.mosip.registration.dto;

import java.util.List;

import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;

public class AuthenticationValidatorDTO {
	private List<FingerprintDetailsDTO> fingerPrintDetails;
	private String userId;
	private String password;
	private String otp;

	public List<FingerprintDetailsDTO> getFingerPrintDetails() {
		return fingerPrintDetails;
	}

	public void setFingerPrintDetails(List<FingerprintDetailsDTO> fingerPrintDetails) {
		this.fingerPrintDetails = fingerPrintDetails;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	
}
