package io.mosip.idRepositoty.fw.util;

import java.io.File;

import io.mosip.authentication.fw.util.IdaScriptsUtil;

public class IdRepoTestsUtil extends IdaScriptsUtil{
	
	/**
	 * The method returns run config path
	 */
	public String getRunConfigFile() {
		return "src/test/resources/idRepository/TestData/RunConfig/runConfiguration.properties";
	}
	
	/**
	 * The method return test data path from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataPath(String className, int index) {
		return getPropertyAsMap(new File("./" + getRunConfigFile()).getAbsolutePath().toString())
				.get(className + ".testDataPath[" + index + "]");
	}

}
