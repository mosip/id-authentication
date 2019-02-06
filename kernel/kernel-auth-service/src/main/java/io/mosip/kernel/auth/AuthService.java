package io.mosip.kernel.auth;

import java.util.List;

import io.mosip.kernel.auth.dto.*;
import io.mosip.kernel.auth.dto.otp.OtpUserDto;
import io.mosip.kernel.auth.dto.otp.OtpValidateRequestDto;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public interface AuthService {
    MosipUserWithTokenDto authenticateUser(LoginUserDto loginUserDto) throws Exception;

    MosipUserWithTokenDto authenticateWithOtp(OtpUserDto otpUserDto) throws Exception;

    MosipUserWithTokenDto authenticateUserWithOtp(LoginUserDto loginUserDto) throws Exception;

    MosipUserWithTokenDto verifyOtp(OtpValidateRequestDto otpValidateRequestDto, String token) throws Exception;

    MosipUserWithTokenDto validateToken(String token) throws Exception;

    Boolean logout(String userName, String token) throws Exception;
    
    RolesListDto getAllRoles() throws Exception;
    
    MosipUserListDto getListOfUsersDetails(List<String> userDetails) throws Exception;
}
