/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateRequestDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateResponseDto;
import io.mosip.kernel.auth.service.OTPGenerateService;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class OTPGenerateServiceImpl implements OTPGenerateService {
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	MosipEnvironment mosipEnvironment;

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.OTPGenerateService#generateOTP(io.mosip.kernel.auth.entities.MosipUserDto, java.lang.String)
	 */
	@Override
	public OtpGenerateResponseDto generateOTP(MosipUserDto mosipUserDto, String channel) {
		try {
			OtpGenerateRequestDto otpGenerateRequestDto = new OtpGenerateRequestDto(mosipUserDto);
			final String url = mosipEnvironment.getOtpManagerSvcUrl() + mosipEnvironment.getGenerateOtpApi();
			OtpGenerateResponseDto otpGenerateResponseDto = restTemplate.postForObject(url, otpGenerateRequestDto,
					OtpGenerateResponseDto.class);
			return otpGenerateResponseDto;
		} catch (Exception err) {
			throw new RuntimeException(err);
		}
	}

}
