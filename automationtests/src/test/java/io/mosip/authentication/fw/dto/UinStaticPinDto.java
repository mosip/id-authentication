package io.mosip.authentication.fw.dto;

import java.util.Map;

/**
 * Dto class to hold static pin for generated UIN during run time execution
 * 
 * @author Vignesh
 *
 */
public class UinStaticPinDto {

	private static Map<String, String> uinStaticPin;

	/**
	 * The method get the stored static pin for the UIN from config file
	 * 
	 * @return map - UIN as key and staticPin as value
	 */
	public static Map<String, String> getUinStaticPin() {
		return uinStaticPin;
	}

	/**
	 * The method store the static pin for UIN in config file
	 * 
	 * @param map - UIN as key and staticPin as value
	 */
	public static void setUinStaticPin(Map<String, String> uinStaticPin) {
		UinStaticPinDto.uinStaticPin = uinStaticPin;
	}

}
