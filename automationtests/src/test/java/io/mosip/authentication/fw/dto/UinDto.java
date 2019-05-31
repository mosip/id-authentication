package io.mosip.authentication.fw.dto;

import java.util.Map;

/**
 * The class or Dto store the generated uin number in config file
 * 
 * @author Vignesh
 *
 */
public class UinDto {
	
	private static Map<String,String> uinData;

	/**
	 * The method get the UIN from property file
	 * 
	 * @return map - UIN as key and Scenario name as value
	 */
	public static Map<String, String> getUinData() {
		return uinData;
	}

	/**
	 * The method store the UIN in property or config file
	 * 
	 * @param map - set UIN as key and Scenario name as value
	 */
	public static void setUinData(Map<String, String> uinData) {
		UinDto.uinData = uinData;
	}

}
