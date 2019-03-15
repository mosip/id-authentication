/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
import io.mosip.kernel.auth.entities.otp.OtpValidatorResponseDto;
import io.mosip.kernel.auth.entities.otp.SmsResponseDto;
import io.mosip.kernel.auth.exception.AuthManagerErrorListException;
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
		SmsResponseDto otpSmsSendResponseDto = null;
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
					+"/"+ mosipEnvironment.getPrimaryLanguage() + mosipEnvironment.getMasterDataOtpTemplate();

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
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			String message = e.getResponseBodyAsString();
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),message);
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
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			String message = e.getResponseBodyAsString();
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),message);
		}
	}

	private OtpEmailSendResponseDto sendOtpByEmail(String message, String email) {
		try {
			String url = mosipEnvironment.getOtpSenderEmailApi();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("mailTo", email);
			map.add("mailSubject", "MOSIP Notification");
			map.add("mailContent",message);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

			ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );
			OtpEmailSendRequestDto otpEmailSendRequestDto = new OtpEmailSendRequestDto(email, message);
			
			OtpEmailSendResponseDto otpEmailSendResponseDto = restTemplate.postForObject(url, otpEmailSendRequestDto,
					OtpEmailSendResponseDto.class);
			return otpEmailSendResponseDto;
		}catch (HttpClientErrorException | HttpServerErrorException e) {
			String errmessage = e.getResponseBodyAsString();
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),errmessage);
		}
	}

	private SmsResponseDto sendOtpBySms(String message, String mobile) {
		try {
			OtpSmsSendRequestDto otpSmsSendRequestDto = new OtpSmsSendRequestDto(mobile, message);
			String url = mosipEnvironment.getOtpSenderSmsApi();
		/*	ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity(otpSmsSendRequestDto,null), String.class);
			System.out.println(res.getBody());*/
			SmsResponseDto otpSmsSendResponseDto = restTemplate.postForObject(url, otpSmsSendRequestDto,
					SmsResponseDto.class);
			return otpSmsSendResponseDto;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			String errmessage = e.getResponseBodyAsString();
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),errmessage);
		}
	}

	@Override
	public MosipUserDtoToken validateOTP(MosipUserDto mosipUser, String otp) {
		String key = new OtpGenerateRequestDto(mosipUser).getKey();
		MosipUserDtoToken mosipUserDtoToken = null;
		ResponseEntity<OtpValidatorResponseDto> response = null;
		final String url = mosipEnvironment.getVerifyOtpUserApi();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("key", key).queryParam("otp",
				otp);
		try
		{
		response = restTemplate.getForEntity(builder.toUriString(), OtpValidatorResponseDto.class);
		}catch (HttpClientErrorException | HttpServerErrorException e) {
			String message = e.getResponseBodyAsString();
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),message);
		}
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			OtpValidatorResponseDto otpValidatorDto = response.getBody();
			if(otpValidatorDto.getStatus()!=null && otpValidatorDto.getStatus().equals("success"))
			{
				BasicTokenDto basicToken = tokenGenerator.basicGenerateOTPToken(mosipUser, true);
				mosipUserDtoToken = new MosipUserDtoToken(mosipUser, basicToken.getAuthToken(),
						basicToken.getRefreshToken(), basicToken.getExpiryTime(), null,null);

				
			}
			else
			{
				mosipUserDtoToken = new MosipUserDtoToken();
				mosipUserDtoToken.setMessage(otpValidatorDto.getMessage());
				mosipUserDtoToken.setStatus(otpValidatorDto.getStatus());
			}
			
		}
		return mosipUserDtoToken;
	}

	@Override
	public AuthNResponseDto sendOTPForUin(MosipUserDto mosipUserDto, List<String> otpChannel, String appId) {
		AuthNResponseDto authNResponseDto = null;
		OtpEmailSendResponseDto otpEmailSendResponseDto = null;
		SmsResponseDto otpSmsSendResponseDto = null;
		String emailMessage = null,mobileMessage = null;
		OtpGenerateResponseDto otpGenerateResponseDto = oTPGenerateService.generateOTP(mosipUserDto);
		for(String channel:otpChannel)
		{
			switch(channel)
			{
			case AuthConstant.EMAIL:
				emailMessage = getOtpEmailMessage(otpGenerateResponseDto, appId);
				otpEmailSendResponseDto = sendOtpByEmail(emailMessage, mosipUserDto.getMail());
			case AuthConstant.PHONE:
				mobileMessage = getOtpSmsMessage(otpGenerateResponseDto, appId);
				otpSmsSendResponseDto = sendOtpBySms(mobileMessage, mosipUserDto.getMobile());
			}		
		}
		if(otpEmailSendResponseDto!=null && otpSmsSendResponseDto!=null)
		{
			AuthNResponseDto authResponseDto = new AuthNResponseDto();
			authResponseDto.setMessage(AuthConstant.UIN_NOTIFICATION_MESSAGE);
		}
		return authNResponseDto;
	}
}
