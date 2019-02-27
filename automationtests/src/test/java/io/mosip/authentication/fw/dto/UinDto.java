package io.mosip.authentication.fw.dto;

import java.util.Map;

/**
 * Store the generated uin number in dto
 * 
 * @author Vignesh
 *
 */
public class UinDto {
	
	private static Map<String,String> uinData;

	public static Map<String, String> getUinData() {
		return uinData;
	}

	public static void setUinData(Map<String, String> uinData) {
		UinDto.uinData = uinData;
	}

}
