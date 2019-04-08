/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateRequestDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateResponseDto;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.exception.AuthManagerServiceException;
import io.mosip.kernel.auth.service.OTPGenerateService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;

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
	
	@Autowired
	private ObjectMapper mapper;

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.OTPGenerateService#generateOTP(io.mosip.kernel.auth.entities.MosipUserDto, java.lang.String)
	 */
	@Override
	public OtpGenerateResponseDto generateOTP(MosipUserDto mosipUserDto) {
		try {
			List<ServiceError> validationErrorsList = null;
			OtpGenerateResponseDto otpGenerateResponseDto;
			OtpGenerateRequestDto otpGenerateRequestDto = new OtpGenerateRequestDto(mosipUserDto);
			final String url = mosipEnvironment.getGenerateOtpApi();
			RequestWrapper<OtpGenerateRequestDto> reqWrapper = new RequestWrapper<>();
			reqWrapper.setRequesttime(LocalDateTime.now());
			reqWrapper.setRequest(otpGenerateRequestDto);
			ResponseEntity<String> response = restTemplate.postForEntity(url, reqWrapper,
					String.class);
			validationErrorsList = ExceptionUtils.getServiceErrorList(response.getBody());  
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			}
			ResponseWrapper<?> responseObject;
			try {
				responseObject = mapper.readValue(response.getBody(), ResponseWrapper.class);
				otpGenerateResponseDto= mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()), OtpGenerateResponseDto.class);
			}catch(Exception e)
			{
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
			}
			return otpGenerateResponseDto;
		} catch (Exception exp) {
			throw new RuntimeException(exp);
		}
	}

}
