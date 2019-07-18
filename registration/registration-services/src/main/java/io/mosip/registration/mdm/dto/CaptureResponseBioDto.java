package io.mosip.registration.mdm.dto;

import java.nio.charset.Charset;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptureResponseBioDto {

	@JsonIgnore
	private CaptureResponsBioDataDto captureResponseData;

	@JsonProperty("data")
	private String captureBioData;
	private String hash;
	private String sessionKey;
	private String signature;
	
	public void setCaptureResponseData() {
		ObjectMapper mapper = new ObjectMapper();
		String str =  new String(Base64.getDecoder().decode(captureBioData.getBytes()), Charset.forName("UTF-8"));
		try {
			captureResponseData = mapper.readValue(str,CaptureResponsBioDataDto.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
