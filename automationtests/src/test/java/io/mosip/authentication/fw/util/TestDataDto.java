package io.mosip.authentication.fw.util;

import java.util.Map;

/**
 * Dto to hold all the testdata from yml file 
 * 
 * @author Vignesh
 *
 */
public class TestDataDto {
	
	public Map<String,Map<String,Map<String,Map<String,Object>>>> testdata;

	public Map<String, Map<String, Map<String, Map<String, Object>>>> getTestdata() {
		return testdata;
	}

	public void setTestdata(Map<String, Map<String, Map<String, Map<String, Object>>>> testdata) {
		this.testdata = testdata;
	}


}

