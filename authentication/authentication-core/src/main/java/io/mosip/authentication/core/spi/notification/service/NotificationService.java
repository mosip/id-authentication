package io.mosip.authentication.core.spi.notification.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;

public interface NotificationService {
	public void sendAuthNotification(AuthRequestDTO authRequestDTO, String uin, AuthResponseDTO authResponseDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, boolean isAuth) throws IdAuthenticationBusinessException ;
	public void sendOtpNotification(OtpRequestDTO otpRequestDto, String otp, String uin,
			String email, String mobileNumber, Map<String, List<IdentityInfoDTO>> idInfo);
	
}
