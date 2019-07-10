package io.mosip.authentication.fw.dto;

import java.util.Map;

public class RidDto {

	private static Map<String,String> ridData;

	/**
	 * The method get the UIN from property file
	 * 
	 * @return map - UIN as key and Scenario name as value
	 */
	public static Map<String, String> getRidData() {
		return ridData;
	}

	/**
	 * The method store the UIN in property or config file
	 * 
	 * @param map - set UIN as key and Scenario name as value
	 */
	public static void setRidData(Map<String, String> ridData) {
		RidDto.ridData = ridData;
	}
}
