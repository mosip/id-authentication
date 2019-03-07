/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.AuthNResponseDto;
import io.mosip.kernel.auth.entities.BasicTokenDto;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.MosipUserDtoToken;

import io.mosip.kernel.auth.entities.otp.OtpEmailSendRequestDto;
import io.mosip.kernel.auth.entities.otp.OtpEmailSendResponseDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateRequestDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateResponseDto;
import io.mosip.kernel.auth.entities.otp.OtpSmsSendRequestDto;
import io.mosip.kernel.auth.entities.otp.OtpSmsSendResponseDto;
import io.mosip.kernel.auth.entities.otp.OtpTemplateDto;
import io.mosip.kernel.auth.entities.otp.OtpTemplateResponseDto;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.entities.otp.OtpValidatorResponseDto;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.jwtBuilder.TokenGenerator;
import io.mosip.kernel.auth.service.OTPGenerateService;
import io.mosip.kernel.auth.service.OTPService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class OTPServiceImpl implements OTPService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.OTPService#sendOTP(io.mosip.kernel.auth.
	 * entities.MosipUserDto, java.lang.String)
	 */

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	MosipEnvironment mosipEnvironment;

	@Autowired
	TokenGenerator tokenGenerator;

	@Autowired
	OTPGenerateService oTPGenerateService;

	@Override
	public AuthNResponseDto sendOTP(MosipUserDto mosipUserDto, List<String> channel, String appId) {
		AuthNResponseDto authNResponseDto = null;
		OtpEmailSendResponseDto otpEmailSendResponseDto = null;
		OtpSmsSendResponseDto otpSmsSendResponseDto = null;
		OtpGenerateResponseDto otpGenerateResponseDto = oTPGenerateService.generateOTP(mosipUserDto);
		if (channel.contains(AuthConstant.EMAIL)) {
			String message = getOtpEmailMessage(otpGenerateResponseDto, appId);
			otpEmailSendResponseDto = sendOtpByEmail(message, mosipUserDto.getMail());
		} else {
			String message = getOtpSmsMessage(otpGenerateResponseDto, appId);
			otpSmsSendResponseDto = sendOtpBySms(message, mosipUserDto.getMobile());
		}
		if (otpEmailSendResponseDto != null) {
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setMessage(otpEmailSendResponseDto.getMessage());
		}
		if (otpSmsSendResponseDto != null) {
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setMessage(otpSmsSendResponseDto.getMessage());
		}
		return authNResponseDto;
	}

	private String getOtpEmailMessage(OtpGenerateResponseDto otpGenerateResponseDto, String appId) {
		try {
			String template = null;
			final String url = mosipEnvironment.getMasterDataTemplateApi()
					+ mosipEnvironment.getPrimaryLanguage() + mosipEnvironment.getMasterDataOtpTemplate();

			OtpTemplateResponseDto otpTemplateResponseDto = restTemplate.getForObject(url,
					OtpTemplateResponseDto.class);
			List<OtpTemplateDto> otpTemplateList = otpTemplateResponseDto.getTemplates();
			for (OtpTemplateDto otpTemplateDto : otpTemplateList) {
				if (otpTemplateDto.getId().toLowerCase().equals(appId.toLowerCase())) {
					template = otpTemplateDto.getFileText();

				}
			}
			String otp = otpGenerateResponseDto.getOtp();
			template = template.replace("$otp", otp);
			return template;
		} catch (Exception err) {
			throw new RuntimeException(err);
		}
	}

	private String getOtpSmsMessage(OtpGenerateResponseDto otpGenerateResponseDto, String appId) {
		try {
			final String url = mosipEnvironment.getMasterDataTemplateApi()
					+"/"+ mosipEnvironment.getPrimaryLanguage() + mosipEnvironment.getMasterDataOtpTemplate();

			OtpTemplateResponseDto otpTemplateResponseDto = restTemplate.getForObject(url,
					OtpTemplateResponseDto.class);
			String template = null;
			List<OtpTemplateDto> otpTemplateList = otpTemplateResponseDto.getTemplates();
			for (OtpTemplateDto otpTemplateDto : otpTemplateList) {
				if (otpTemplateDto.getId().toLowerCase().equals(appId.toLowerCase())) {
					template = otpTemplateDto.getFileText();

				}
			}
			String otp = otpGenerateResponseDto.getOtp();
			template = template.replace("$otp", otp);
			return template;
		} catch (Exception err) {
			throw new RuntimeException(err);
		}
	}

	private OtpEmailSendResponseDto sendOtpByEmail(String message, String email) {
		try {
			OtpEmailSendRequestDto otpEmailSendRequestDto = new OtpEmailSendRequestDto(email, message);
			String url = mosipEnvironment.getOtpSenderEmailApi();
			OtpEmailSendResponseDto otpEmailSendResponseDto = restTemplate.postForObject(url, otpEmailSendRequestDto,
					OtpEmailSendResponseDto.class);
			return otpEmailSendResponseDto;
		} catch (Exception err) {
			throw new RuntimeException(err);
		}
	}

	private OtpSmsSendResponseDto sendOtpBySms(String message, String mobile) {
		try {
			OtpSmsSendRequestDto otpSmsSendRequestDto = new OtpSmsSendRequestDto(mobile, message);
			String url = mosipEnvironment.getOtpSenderSmsApi();
			OtpSmsSendResponseDto otpSmsSendResponseDto = restTemplate.postForObject(url, otpSmsSendRequestDto,
					OtpSmsSendResponseDto.class);
			return otpSmsSendResponseDto;
		} catch (Exception err) {
			throw new RuntimeException(err);
		}
	}

	@Override
	public MosipUserDtoToken validateOTP(MosipUserDto mosipUser, String otp) {
		String key = new OtpGenerateRequestDto(mosipUser).getKey();
		MosipUserDtoToken mosipUserDtoToken = null;
		final String url = mosipEnvironment.getVerifyOtpUserApi();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("key", key).queryParam("otp",
				otp);
		System.out.println(builder.toUriString());
		//ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null,String.class);
		ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
		String responseBody = response.getBody();
		List<ServiceError> validationErrorsList=null;
		try {
			validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		} catch (Exception e) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
		}
		if(validationErrorsList!=null && validationErrorsList.size()>0)
		{
			throw new AuthManagerException(validationErrorsList);
		}
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			BasicTokenDto basicToken = tokenGenerator.basicGenerateOTPToken(mosipUser, true);
			mosipUserDtoToken = new MosipUserDtoToken(mosipUser, basicToken.getAuthToken(),
					basicToken.getRefreshToken(), basicToken.getExpiryTime(), null);
		}
		return mosipUserDtoToken;
	}
}
