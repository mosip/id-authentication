/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.OTPErrorCode;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.entities.otp.OtpValidatorResponseDto;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.exception.AuthManagerServiceException;
import io.mosip.kernel.auth.service.UinService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.idrepo.dto.IdResponseDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author M1049825
 *
 */

@Component
public class UinServiceImpl implements UinService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	MosipEnvironment env;
	
	@Autowired
	private ObjectMapper mapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.UinService#getDetailsFromUin(io.mosip.kernel
	 * .auth.entities.otp.OtpUser)
	 */
	@Override
	public MosipUserDto getDetailsFromUin(OtpUser otpUser) throws Exception {
		MosipUserDto mosipDto = new MosipUserDto();
		IdResponseDTO idResponse = null;
		mosipDto.setUserId(otpUser.getUserId());
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put(AuthConstant.APPTYPE_UIN.toLowerCase(), otpUser.getUserId());
		ResponseEntity<String> response = restTemplate.getForEntity(
				UriComponentsBuilder.fromHttpUrl(env.getUinGetDetailsUrl()).buildAndExpand(uriParams).toUriString(),
				String.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			String responseBody = response.getBody();
			List<ServiceError> validationErrorsList = null;
				validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
	        
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			}
			
			try {
				idResponse = mapper.readValue(responseBody, IdResponseDTO.class);
			}catch(Exception e)
			{
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
			}
		}
			JSONObject res = (JSONObject) idResponse.getResponse().getIdentity();
			if((String) res.get("phone")!=null)
			{
				mosipDto.setMobile((String) res.get("phone"));
			}
			else
			{
				throw new AuthManagerException(OTPErrorCode.PHONENOTREGISTERED.getErrorCode(),OTPErrorCode.PHONENOTREGISTERED.getErrorMessage());
			}
			if((String) res.get("email")!=null)
			{
				mosipDto.setMobile((String) res.get("email"));
			}
			else
			{
				throw new AuthManagerException(OTPErrorCode.EMAILNOTREGISTERED.getErrorCode(),OTPErrorCode.EMAILNOTREGISTERED.getErrorMessage());
			}
			if((String) res.get("phone")==null && (String) res.get("email")!=null)
			{
				throw new AuthManagerException(OTPErrorCode.EMAILPHONENOTREGISTERED.getErrorCode(),OTPErrorCode.EMAILPHONENOTREGISTERED.getErrorMessage());
			}
			
		return mosipDto;
	}

}
