/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.AuthNResponseDto;
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
import io.mosip.kernel.auth.entities.otp.OtpValidateResponseDto;
import io.mosip.kernel.auth.jwtBuilder.TokenGenerator;
import io.mosip.kernel.auth.service.OTPGenerateService;
import io.mosip.kernel.auth.service.OTPService;

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
	public AuthNResponseDto sendOTP(MosipUserDto mosipUserDto, String channel) {
		AuthNResponseDto authNResponseDto = null;
		OtpEmailSendResponseDto otpEmailSendResponseDto = null;
		OtpSmsSendResponseDto otpSmsSendResponseDto = null;
		OtpGenerateResponseDto otpGenerateResponseDto = oTPGenerateService.generateOTP(mosipUserDto, channel);
		if (channel.equals(AuthConstant.EMAIL)) {
			String message = getOtpEmailMessage(otpGenerateResponseDto);
			otpEmailSendResponseDto = sendOtpByEmail(message, mosipUserDto.getMail());
		} else {
			String message = getOtpSmsMessage(otpGenerateResponseDto);
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

	private String getOtpEmailMessage(OtpGenerateResponseDto otpGenerateResponseDto) {
		try {
			final String url = mosipEnvironment.getMasterDataUrl() + mosipEnvironment.getMasterDataTemplateApi() + "eng"
					+ mosipEnvironment.getMasterDataOtpTemplate();

			OtpTemplateResponseDto otpTemplateResponseDto = restTemplate.getForObject(url,
					OtpTemplateResponseDto.class);
			OtpTemplateDto otpTemplateDto = otpTemplateResponseDto.getTemplates().get(0);
			String template = otpTemplateDto.getFileText();
			template.replace("$otp", otpGenerateResponseDto.getOtp());
			return template;
		} catch (Exception err) {
			throw new RuntimeException(err);
		}
	}

	private String getOtpSmsMessage(OtpGenerateResponseDto otpGenerateResponseDto) {
		try {
			final String url = mosipEnvironment.getMasterDataUrl() + mosipEnvironment.getMasterDataTemplateApi() + "eng"
					+ mosipEnvironment.getMasterDataOtpTemplate();

			OtpTemplateResponseDto otpTemplateResponseDto = restTemplate.getForObject(url,
					OtpTemplateResponseDto.class);
			OtpTemplateDto otpTemplateDto = otpTemplateResponseDto.getTemplates().get(0);
			String template = otpTemplateDto.getFileText();
			template.replace("$otp", otpGenerateResponseDto.getOtp());
			return template;
		} catch (Exception err) {
			throw new RuntimeException(err);
		}
	}

	private OtpEmailSendResponseDto sendOtpByEmail(String message, String email) {
		try {
			OtpEmailSendRequestDto otpEmailSendRequestDto = new OtpEmailSendRequestDto(email, message);
			String url = mosipEnvironment.getOtpSenderSvcUrl() + mosipEnvironment.getOtpSenderEmailApi();
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
			String url = mosipEnvironment.getOtpSenderSvcUrl() + mosipEnvironment.getOtpSenderSmsApi();
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
		String verified_token = null;

		final String url = mosipEnvironment.getOtpManagerSvcUrl() + mosipEnvironment.getVerifyOtpUserApi();
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("key", key).queryParam("otp",
				otp);

		OtpValidateResponseDto otpValidateResponseDto = restTemplate.getForObject(builder.toUriString(),
				OtpValidateResponseDto.class);
		if (otpValidateResponseDto != null) {
			verified_token = tokenGenerator.generateForOtp(mosipUser, true);
			mosipUserDtoToken = new MosipUserDtoToken(mosipUser, verified_token, otpValidateResponseDto.getMessage());
		}
		return mosipUserDtoToken;
	}
}
