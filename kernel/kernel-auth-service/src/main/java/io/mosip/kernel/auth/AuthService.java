package io.mosip.kernel.auth;

import io.mosip.kernel.auth.entities.*;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.entities.otp.OtpValidateRequestDto;

public interface AuthService {
    MosipUserWithToken authenticateUser(LoginUser loginUser) throws Exception;

    MosipUserWithToken authenticateWithOtp(OtpUser otpUser) throws Exception;

    MosipUserWithToken authenticateUserWithOtp(LoginUser loginUser) throws Exception;

    MosipUserWithToken verifyOtp(OtpValidateRequestDto otpValidateRequestDto, String token) throws Exception;

    MosipUserWithToken validateToken(String token) throws Exception;

    Boolean logout(String userName, String token) throws Exception;
}
