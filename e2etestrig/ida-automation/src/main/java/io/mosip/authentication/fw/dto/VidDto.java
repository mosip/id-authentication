package io.mosip.authentication.fw.dto;

import java.util.Map;

/**
 * The class store and retrieve the generated VID for the UIN from config file
 * 
 * @author Vignesh
 *
 */
public class VidDto {

	private static Map<String, String> vid;

	/**
	 * The method retrieve the generated VID from config file
	 * 
	 * @return map - VID as key and UIN as value
	 */
	public static Map<String, String> getVid() {
		return vid;
	}

	/**
	 * The method store the generated VID in config file
	 * 
	 * @param map - VID as key and UIN as value
	 */
	public static void setVid(Map<String, String> vid) {
		VidDto.vid = vid;
	}

}
