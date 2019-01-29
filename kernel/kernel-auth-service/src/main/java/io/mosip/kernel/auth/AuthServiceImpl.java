package io.mosip.kernel.auth;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.entities.*;
import io.mosip.kernel.auth.entities.otp.*;
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
    RestTemplate restTemplate;

    @Autowired
    MosipEnvironment mosipEnvironment;

    private MosipUser authenticateWithLdap(LoginUser loginUser) {
        try {
            final String url = mosipEnvironment.getLdapSvcUrl() + mosipEnvironment.getLdapAuthenticate();
            MosipUser mosipUser = restTemplate.postForObject(url, loginUser, MosipUser.class);
            if (mosipUser == null) {
                throw new RuntimeException("Please enter valid user credentials");
            }
            return mosipUser;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private MosipUser verifyOtpUserLdap(OtpUser otpUser) {
        try {
            final String url = mosipEnvironment.getLdapSvcUrl() + mosipEnvironment.getLdapVerifyOtpUser();
            MosipUser mosipUser = restTemplate.postForObject(url, otpUser, MosipUser.class);
            if (mosipUser == null) {
                throw new RuntimeException("Please enter valid user credentials");
            }
            return mosipUser;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private OtpGenerateResponseDto generateOtp(MosipUser mosipUser) {
        try {
            OtpGenerateRequestDto otpGenerateRequestDto = new OtpGenerateRequestDto(mosipUser);
            final String url = mosipEnvironment.getOtpManagerSvcUrl() + mosipEnvironment.getGenerateOtpApi();
            OtpGenerateResponseDto otpGenerateResponseDto = restTemplate.postForObject(url, otpGenerateRequestDto, OtpGenerateResponseDto.class);
            return otpGenerateResponseDto;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private String getOtpEmailMessage(OtpGenerateResponseDto otpGenerateResponseDto, OtpUser otpUser) {
        try {
            final String url = mosipEnvironment.getMasterDataUrl()
                    + mosipEnvironment.getMasterDataTemplateApi()
                    + otpUser.getLangCode()
                    + mosipEnvironment.getMasterDataOtpTemplate();

            OtpTemplateResponseDto otpTemplateResponseDto = restTemplate.getForObject(url, OtpTemplateResponseDto.class);
            OtpTemplateDto otpTemplateDto = otpTemplateResponseDto.getTemplates().get(0);
            String template = otpTemplateDto.getFileText();
            template.replace("$otp", otpGenerateResponseDto.getOtp());
            return template;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private String getOtpSmsMessage(OtpGenerateResponseDto otpGenerateResponseDto, OtpUser otpUser) {
        try {
            final String url = mosipEnvironment.getMasterDataUrl()
                    + mosipEnvironment.getMasterDataTemplateApi()
                    + otpUser.getLangCode()
                    + mosipEnvironment.getMasterDataOtpTemplate();

            OtpTemplateResponseDto otpTemplateResponseDto = restTemplate.getForObject(url, OtpTemplateResponseDto.class);
            OtpTemplateDto otpTemplateDto = otpTemplateResponseDto.getTemplates().get(0);
            String template = otpTemplateDto.getFileText();
            template.replace("$otp", otpGenerateResponseDto.getOtp());
            return template;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private OtpEmailSendResponseDto sendOtpByEmail(String message, OtpUser otpUser) {
        try {
            OtpEmailSendRequestDto otpEmailSendRequestDto = new OtpEmailSendRequestDto(otpUser.getEmail(), message);
            String url = mosipEnvironment.getOtpSenderSvcUrl() + mosipEnvironment.getOtpSenderEmailApi();
            OtpEmailSendResponseDto otpEmailSendResponseDto = restTemplate.postForObject(url, otpEmailSendRequestDto, OtpEmailSendResponseDto.class);
            return otpEmailSendResponseDto;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private OtpSmsSendResponseDto sendOtpBySms(String message, OtpUser otpUser) {
        try {
            OtpSmsSendRequestDto otpSmsSendRequestDto = new OtpSmsSendRequestDto(otpUser.getNumber(), message);
            String url = mosipEnvironment.getOtpSenderSvcUrl() + mosipEnvironment.getOtpSenderSmsApi();
            OtpSmsSendResponseDto otpSmsSendResponseDto = restTemplate.postForObject(url, otpSmsSendRequestDto, OtpSmsSendResponseDto.class);
            return otpSmsSendResponseDto;
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
    public MosipUserWithToken authenticateWithOtp(OtpUser otpUser) throws Exception {
        MosipUser mosipUser = verifyOtpUserLdap(otpUser);
        OtpGenerateResponseDto otpGenerateResponseDto = generateOtp(mosipUser);
        if (otpUser.getOtpChannel().equals("EMAIL")) {
            String message = getOtpEmailMessage(otpGenerateResponseDto, otpUser);
            OtpEmailSendResponseDto otpEmailSendResponseDto = sendOtpByEmail(message, otpUser);
        } else {
            String message = getOtpSmsMessage(otpGenerateResponseDto, otpUser);
            OtpSmsSendResponseDto otpSmsSendResponseDto = sendOtpBySms(message, otpUser);
        }

        String token = tokenGenerator.generateForOtp(mosipUser, false);
        return new MosipUserWithToken(mosipUser, token);
    }

    @Override
    public MosipUserWithToken authenticateUserWithOtp(LoginUser loginUser) throws Exception {
        MosipUser mosipUser = authenticateWithLdap(loginUser);
        OtpGenerateResponseDto otpGenerateResponseDto = generateOtp(mosipUser);
        String token = tokenGenerator.generateForOtp(mosipUser, false);
        return new MosipUserWithToken(mosipUser, token);
    }

    @Override
    public MosipUserWithToken verifyOtp(OtpValidateRequestDto otpValidateRequestDto, String token) throws Exception {
        try {
            MosipUserWithToken mosipUserWithToken = tokenValidator.validateForOtpVerification(token);
            String key = new OtpGenerateRequestDto(mosipUserWithToken.getMosipUser()).getKey();

            final String url = mosipEnvironment.getOtpManagerSvcUrl() + mosipEnvironment.getVerifyOtpUserApi();
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("key", key)
                    .queryParam("otp", otpValidateRequestDto.getOtp());

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
