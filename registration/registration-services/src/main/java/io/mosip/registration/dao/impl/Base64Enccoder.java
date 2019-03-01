package io.mosip.registration.dao.impl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.dto.UserDetailDto;
import io.mosip.registration.dto.UserDetailResponseDto;

public class Base64Enccoder {

	public static void main(String[] args) {
		
		final String url = "http://integ.mosip.io/syncdata/v1.0/userdetails/{regid}";
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("regid", "10031");
		//params.put("lastUpdated", "2018-12-28T09:25:43.204Z");

		HttpEntity entity = new HttpEntity(headers);

		HttpEntity<UserDetailResponseDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserDetailResponseDto.class,params);

		//System.out.println(response.getBody());
		
		UserDetailResponseDto respons=response.getBody();
		UserDetailDto user=respons.getUserDetails().get(0);
		
		String msg=new String(user.getUserPassword(),StandardCharsets.UTF_8);
		
		System.out.println(msg);
		//System.out.println(CryptoUtil.decodeBase64("e1NTSEE1MTJ9TkhVb1c2WHpkZVJCa0drbU9tTk9ZcElvdUlNRGl5ODlJK3RhNm04d0FlTWhMSEoyTG4wSVJkNEJ2dkNqVFg4bTBuV2ZySStneXBTVittbVJKWnAxTkFwT3BWY3MxTVU5"));
		
	}

}
