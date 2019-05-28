package io.mosip.authentication.fw.dto;

import java.util.Map;

/**
 * The class or dto store and retrieve the staticPin for the VID from config
 * file
 * 
 * @author Vignesh
 *
 */
public class VidStaticPinDto {

	private static Map<String, String> vidStaticPin;

	/**
	 * The method retrieve the static pin for VID from config file
	 * 
	 * @return map - VID as key and StaticPin as value
	 */
	public static Map<String, String> getVidStaticPin() {
		return vidStaticPin;
	}

	/**
	 * The method store the static pin for VID in config file
	 * 
	 * @param map - VID as key and StaticPin as value
	 */
	public static void setVidStaticPin(Map<String, String> vidStaticPin) {
		VidStaticPinDto.vidStaticPin = vidStaticPin;
	}

}
