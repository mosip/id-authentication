package io.mosip.authentication.fw.dto;

import java.util.Map;

/**
 * Dto class to hold static pin for generated UIN during run time execution
 * 
 * @author Vignesh
 *
 */
public class UinStaticPinDto {
	
	private static Map<String,String> uinStaticPin;

	public static Map<String, String> getUinStaticPin() {
		return uinStaticPin;
	}

	public static void setUinStaticPin(Map<String, String> uinStaticPin) {
		UinStaticPinDto.uinStaticPin = uinStaticPin;
	}

}

