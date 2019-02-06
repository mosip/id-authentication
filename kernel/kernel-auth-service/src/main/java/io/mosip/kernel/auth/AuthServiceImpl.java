package io.mosip.kernel.auth;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.dto.*;
import io.mosip.kernel.auth.dto.otp.*;
import io.mosip.kernel.auth.jwtBuilder.TokenGenerator;
import io.mosip.kernel.auth.jwtBuilder.TokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
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

    private MosipUserDto authenticateWithLdap(LoginUserDto loginUserDto) {
        try {
            final String url = mosipEnvironment.getLdapSvcUrl() + mosipEnvironment.getLdapAuthenticate();
            MosipUserDto mosipUserDto = restTemplate.postForObject(url, loginUserDto, MosipUserDto.class);
            if (mosipUserDto == null) {
                throw new RuntimeException("Please enter valid user credentials");
            }
            return mosipUserDto;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private MosipUserDto verifyOtpUserLdap(OtpUserDto otpUserDto) {
        try {
            final String url = mosipEnvironment.getLdapSvcUrl() + mosipEnvironment.getLdapVerifyOtpUser();
            MosipUserDto mosipUserDto = restTemplate.postForObject(url, otpUserDto, MosipUserDto.class);
            if (mosipUserDto == null) {
                throw new RuntimeException("Please enter valid user credentials");
            }
            return mosipUserDto;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private OtpGenerateResponseDto generateOtp(MosipUserDto mosipUserDto) {
        try {
            OtpGenerateRequestDto otpGenerateRequestDto = new OtpGenerateRequestDto(mosipUserDto);
            final String url = mosipEnvironment.getOtpManagerSvcUrl() + mosipEnvironment.getGenerateOtpApi();
            OtpGenerateResponseDto otpGenerateResponseDto = restTemplate.postForObject(url, otpGenerateRequestDto, OtpGenerateResponseDto.class);
            return otpGenerateResponseDto;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private String getOtpEmailMessage(OtpGenerateResponseDto otpGenerateResponseDto, OtpUserDto otpUserDto) {
        try {
            final String url = mosipEnvironment.getMasterDataUrl()
                    + mosipEnvironment.getMasterDataTemplateApi()
                    + otpUserDto.getLangCode()
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

    private String getOtpSmsMessage(OtpGenerateResponseDto otpGenerateResponseDto, OtpUserDto otpUserDto) {
        try {
            final String url = mosipEnvironment.getMasterDataUrl()
                    + mosipEnvironment.getMasterDataTemplateApi()
                    + otpUserDto.getLangCode()
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

    private OtpEmailSendResponseDto sendOtpByEmail(String message, OtpUserDto otpUserDto) {
        try {
            OtpEmailSendRequestDto otpEmailSendRequestDto = new OtpEmailSendRequestDto(otpUserDto.getEmail(), message);
            String url = mosipEnvironment.getOtpSenderSvcUrl() + mosipEnvironment.getOtpSenderEmailApi();
            OtpEmailSendResponseDto otpEmailSendResponseDto = restTemplate.postForObject(url, otpEmailSendRequestDto, OtpEmailSendResponseDto.class);
            return otpEmailSendResponseDto;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    private OtpSmsSendResponseDto sendOtpBySms(String message, OtpUserDto otpUserDto) {
        try {
            OtpSmsSendRequestDto otpSmsSendRequestDto = new OtpSmsSendRequestDto(otpUserDto.getNumber(), message);
            String url = mosipEnvironment.getOtpSenderSvcUrl() + mosipEnvironment.getOtpSenderSmsApi();
            OtpSmsSendResponseDto otpSmsSendResponseDto = restTemplate.postForObject(url, otpSmsSendRequestDto, OtpSmsSendResponseDto.class);
            return otpSmsSendResponseDto;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    @Override
    public MosipUserWithTokenDto authenticateUser(LoginUserDto loginUserDto) throws Exception {
        MosipUserDto mosipUserDto = authenticateWithLdap(loginUserDto);
        String token = tokenGenerator.basicGenerate(mosipUserDto);
        return new MosipUserWithTokenDto(mosipUserDto, token);
    }

    @Override
    public MosipUserWithTokenDto authenticateWithOtp(OtpUserDto otpUserDto) throws Exception {
        MosipUserDto mosipUserDto = verifyOtpUserLdap(otpUserDto);
        OtpGenerateResponseDto otpGenerateResponseDto = generateOtp(mosipUserDto);
        if (otpUserDto.getOtpChannel().equals("EMAIL")) {
            String message = getOtpEmailMessage(otpGenerateResponseDto, otpUserDto);
            OtpEmailSendResponseDto otpEmailSendResponseDto = sendOtpByEmail(message, otpUserDto);
        } else {
            String message = getOtpSmsMessage(otpGenerateResponseDto, otpUserDto);
            OtpSmsSendResponseDto otpSmsSendResponseDto = sendOtpBySms(message, otpUserDto);
        }

        String token = tokenGenerator.generateForOtp(mosipUserDto, false);
        return new MosipUserWithTokenDto(mosipUserDto, token);
    }

    @Override
    public MosipUserWithTokenDto authenticateUserWithOtp(LoginUserDto loginUserDto) throws Exception {
        MosipUserDto mosipUserDto = authenticateWithLdap(loginUserDto);
        OtpGenerateResponseDto otpGenerateResponseDto = generateOtp(mosipUserDto);
        String token = tokenGenerator.generateForOtp(mosipUserDto, false);
        return new MosipUserWithTokenDto(mosipUserDto, token);
    }

    @Override
    public MosipUserWithTokenDto verifyOtp(OtpValidateRequestDto otpValidateRequestDto, String token) throws Exception {
        try {
            MosipUserWithTokenDto mosipUserWithTokenDto = tokenValidator.validateForOtpVerification(token);
            String key = new OtpGenerateRequestDto(mosipUserWithTokenDto.getMosipUserDto()).getKey();

            final String url = mosipEnvironment.getOtpManagerSvcUrl() + mosipEnvironment.getVerifyOtpUserApi();
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("key", key)
                    .queryParam("otp", otpValidateRequestDto.getOtp());

            restTemplate.getForObject(builder.toUriString(), OtpValidateResponseDto.class);
            String verified_token = tokenGenerator.generateForOtp(mosipUserWithTokenDto.getMosipUserDto(), true);
            return new MosipUserWithTokenDto(mosipUserWithTokenDto.getMosipUserDto(), verified_token);
        } catch (Exception err) {
            throw new Exception(err);
        }
    }

    @Override
    public MosipUserWithTokenDto validateToken(String token) throws Exception {
        return tokenValidator.basicValidate(token);
    }

    @Override
    public Boolean logout(String userName, String token) {
        MosipUserWithTokenDto mosipUserWithTokenDto = tokenValidator.basicValidate(token);
        return mosipUserWithTokenDto != null;
    }

    @Override
    public RolesListDto getAllRoles() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(mosipEnvironment.getLdapSvcUrl()).append(mosipEnvironment.getLdapAllRolesUrl());
        try {
            ResponseEntity<RolesListDto> rolesResponse = restTemplate.getForEntity(builder.toString(), RolesListDto.class);
            if (rolesResponse.getStatusCode().is2xxSuccessful()) {
                return rolesResponse.getBody();
            } else {
                throw new RuntimeException(rolesResponse.getStatusCode() + " :Error Code");
            }
        } catch (RestClientException ex) {
            throw new Exception(ex);
        }
    }

    @Override
    public MosipUserListDto getListOfUsersDetails(List<String> userDetail) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(mosipEnvironment.getLdapSvcUrl()).append(mosipEnvironment.getLdapAllUsersUrl());
        try {
            ResponseEntity<MosipUserListDto> userDetailResponse = restTemplate.postForEntity(builder.toString(), userDetail, MosipUserListDto.class);
            if (userDetailResponse.getStatusCode().is2xxSuccessful()) {
                return userDetailResponse.getBody();
            } else {
                throw new RuntimeException(userDetailResponse.getStatusCode() + " :Error Code");
            }
        } catch (RestClientException ex) {
            throw new Exception(ex);
        }

    }
}
