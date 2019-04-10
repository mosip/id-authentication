/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.service.UinService;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Ramadurai Pandian
 *
 */

@Component
public class UinServiceImpl implements UinService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	MosipEnvironment env;

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
		mosipDto.setUserId(otpUser.getUserId());
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put(AuthConstant.APPTYPE_UIN.toLowerCase(), otpUser.getUserId());
		ResponseEntity<String> response = restTemplate.getForEntity(
				UriComponentsBuilder.fromHttpUrl(env.getUinGetDetailsUrl()).buildAndExpand(uriParams).toUriString(),
				String.class);
		String responseBody = response.getBody();
		JSONObject json = new JSONObject(responseBody);
		JSONObject res = (JSONObject) json.get("response");
		JSONObject identity = (JSONObject) res.get("identity");
		mosipDto.setMobile((String) identity.get("phone"));
		mosipDto.setMail((String) identity.get("email"));
		return mosipDto;
	}

}
