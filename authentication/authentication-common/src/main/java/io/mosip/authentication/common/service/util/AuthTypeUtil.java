package io.mosip.authentication.common.service.util;

import java.util.stream.Stream;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
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

}
