package io.mosip.kernel.auth;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.entities.*;
import io.mosip.kernel.auth.jwtBuilder.TokenGenerator;
import io.mosip.kernel.auth.jwtBuilder.TokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AuthServiceImpl implements AuthService {

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    TokenValidator tokenValidator;

    @Autowired
    MosipEnvironment mosipEnvironment;

    public MosipUser authenticateWithLdap(LoginUser loginUser) {
        try {
            final String uri = mosipEnvironment.getLdapSvcUrl() + mosipEnvironment.getLdapAuthenticate();
            RestTemplate restTemplate = new RestTemplate();
            MosipUser mosipUser = restTemplate.postForObject(uri, loginUser, MosipUser.class);
            if (mosipUser == null) {
                throw new RuntimeException("Please enter valid user credentials");
            }
            return mosipUser;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private void triggerOtp(MosipUser mosipUser) {
        try {
            OtpTriggerRequestDto otpTriggerRequestDto = new OtpTriggerRequestDto(mosipUser);
            final String uri = mosipEnvironment.getOtpSvcUrl() + mosipEnvironment.getTriggerOtpApi();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(uri, otpTriggerRequestDto, OtpTriggerResponseDto.class);
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    @Override
    public MosipUserWithToken authenticateUser(LoginUser loginUser) throws Exception {
        MosipUser mosipUser = authenticateWithLdap(loginUser);
        String token = tokenGenerator.basicGenerate(mosipUser);
        return new MosipUserWithToken(mosipUser, token);
    }

    @Override
    public MosipUserWithToken authenticateUserWithOtp(LoginUser loginUser) throws Exception {
        MosipUser mosipUser = authenticateWithLdap(loginUser);
        triggerOtp(mosipUser);
        String token = tokenGenerator.generateForOtp(mosipUser, false);
        return new MosipUserWithToken(mosipUser, token);
    }

    @Override
    public MosipUserWithToken verifyOtp(OtpUser otpUser, String token) throws Exception {
        try {
            MosipUserWithToken mosipUserWithToken = tokenValidator.validateForOtpVerification(token);
            String key = new OtpTriggerRequestDto(mosipUserWithToken.getMosipUser()).getKey();

            final String url = mosipEnvironment.getOtpSvcUrl() + mosipEnvironment.getVerifyOtpApi();
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("key", key)
                    .queryParam("otp", otpUser.getOtp());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(builder.toUriString(), OtpValidateResponseDto.class);
            String verified_token = tokenGenerator.generateForOtp(mosipUserWithToken.getMosipUser(), true);
            return new MosipUserWithToken(mosipUserWithToken.getMosipUser(), verified_token);
        } catch (Exception err) {
            throw new Exception(err);
        }
    }

    @Override
    public MosipUserWithToken validateToken(String token) throws Exception {
        return tokenValidator.basicValidate(token);
    }

    @Override
    public Boolean logout(String userName, String token) {
        MosipUserWithToken mosipUserWithToken = tokenValidator.basicValidate(token);
        return mosipUserWithToken != null;
    }
}
