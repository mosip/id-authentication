package io.mosip.registration.service;

import io.mosip.registration.util.common.OTPManager;

public class RestCall {
	

	public static void main(String[] args) {
		
		OTPManager otp= new OTPManager();
		otp.getOTP("110011");
	}

}
