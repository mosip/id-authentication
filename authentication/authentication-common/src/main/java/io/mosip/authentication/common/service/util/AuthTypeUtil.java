package io.mosip.authentication.common.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;

public final class AuthTypeUtil {
	
	private AuthTypeUtil() {}
	
	public static boolean isBio(AuthRequestDTO authReqDto) {
		return isAuthTypeInfoAvailable(authReqDto, BioAuthType.values());
	}
	
	public static boolean isDemo(AuthRequestDTO authReqDto) {
		return isAuthTypeInfoAvailable(authReqDto, DemoAuthType.values());
	}
	
	public static boolean isOtp(AuthRequestDTO authReqDto) {
		return isAuthTypeInfoAvailable(authReqDto, new AuthType[] {PinAuthType.OTP});
	}
	
	public static boolean isPin(AuthRequestDTO authReqDto) {
		return isAuthTypeInfoAvailable(authReqDto, new AuthType[] {PinAuthType.SPIN});
	}

	private static boolean isAuthTypeInfoAvailable(AuthRequestDTO authReqDto, AuthType[] values) {
		return Stream.of(values).anyMatch(authType -> authType.isAuthTypeInfoAvailable(authReqDto));
	}
	
	public static List<RequestType> findAutRequestTypes(AuthRequestDTO authRequestDTO, Environment env) {
		List<RequestType> requestTypes = new ArrayList<>();
		if(AuthTypeUtil.isOtp(authRequestDTO)) {
			requestTypes.add(RequestType.OTP_AUTH);
		}
		if(AuthTypeUtil.isDemo(authRequestDTO)) {
			requestTypes.add(RequestType.DEMO_AUTH);
		}
		if(AuthTypeUtil.isBio(authRequestDTO)) {
			if (AuthTransactionHelper.isFingerAuth(authRequestDTO, env)) {
				requestTypes.add(RequestType.FINGER_AUTH);
			}
			if (AuthTransactionHelper.isIrisAuth(authRequestDTO, env)) {
				requestTypes.add(RequestType.IRIS_AUTH);
			}
			if (AuthTransactionHelper.isFaceAuth(authRequestDTO, env)) {
				requestTypes.add(RequestType.FACE_AUTH);
			}
		}
		if(authRequestDTO instanceof KycAuthRequestDTO) {
			requestTypes.add(RequestType.KYC_AUTH_REQUEST);
		}
		
		return requestTypes;
	}

}
