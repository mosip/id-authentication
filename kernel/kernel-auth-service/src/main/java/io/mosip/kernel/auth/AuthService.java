package io.mosip.kernel.auth;

import io.mosip.kernel.auth.entities.MosipUser;
import io.mosip.kernel.auth.entities.MosipUserWithToken;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.OtpUser;

public interface AuthService {
    MosipUserWithToken authenticateUser(LoginUser user) throws Exception;

    MosipUserWithToken authenticateUserWithOtp(LoginUser user) throws Exception;

    MosipUserWithToken verifyOtp(OtpUser otpUser, String token) throws Exception;

    MosipUserWithToken validateToken(String token) throws Exception;

    Boolean logout(String userName, String token) throws Exception;
}
