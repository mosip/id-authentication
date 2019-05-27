package io.mosip.authentication.fw.dto;

import java.util.Map;

/**
 * Class hold all the errorCode,errorMessage as mandatory and actionCode,
 * actionMessage if necessary
 * 
 * @author Athila
 *
 */
public class ErrorsDto {
	
	private static Map<String,Map<String,Map<String,String>>> errors;
	
	/**
	 * The method gets errorCode,errorMessage for key or input from TestData
	 * 
	 * @return collection - errorCode,errorMessage
	 */
	public static Map<String, Map<String, Map<String, String>>> getErrors() {
		return errors;
	}

	/**
	 * The method set the errorCode, errorMessage available in yml file in
	 * standardized format
	 * 
	 * @param errors - collection of error fromm YML
	 */
	public static void setErrors(Map<String, Map<String, Map<String, String>>> errors) {
		ErrorsDto.errors = errors;
	}
}
