package io.mosip.authentication.testdata;

import java.util.Map;

/**
 * Dto to hold all the test data from yml file
 * 
 * @author Athila
 *
 */
public class TestDataDto {
	
	public static Map<String,Map<String,Map<String,Map<String,Object>>>> testdata;
	/**
	 * The method get loaded test data
	 * 
	 * @return map
	 */
	public static Map<String, Map<String, Map<String, Map<String, Object>>>> getTestdata() {
		return testdata;
	}
    /**
     * The method set loaded test data
     * 
     * @param testdata
     */
	public static void setTestdata(Map<String, Map<String, Map<String, Map<String, Object>>>> testdata) {
		TestDataDto.testdata = testdata;
	}
}
