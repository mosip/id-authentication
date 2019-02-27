package io.mosip.authentication.testdata;

import java.util.Map;

/**
 * Dto to hold all the test data from yml file
 * 
 * @author Vignesh
 *
 */
public class TestDataDto {
	
	public static Map<String,Map<String,Map<String,Map<String,Object>>>> testdata;

	public static Map<String, Map<String, Map<String, Map<String, Object>>>> getTestdata() {
		return testdata;
	}

	public static void setTestdata(Map<String, Map<String, Map<String, Map<String, Object>>>> testdata) {
		TestDataDto.testdata = testdata;
	}
}
