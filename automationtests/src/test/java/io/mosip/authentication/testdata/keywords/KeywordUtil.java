package io.mosip.authentication.testdata.keywords;

import java.util.Map;

/**
 * Absrtract class is to precondtion file as per keyword implementation for ida,
 * prereg, reg, kernal module
 * 
 * @author Vignesh
 *
 */
public abstract class KeywordUtil {
	
	/**
	 * Method get updated test data value for the keyword
	 * 
	 * @param map
	 * @return map
	 */
	public abstract Map<String, String> precondtionKeywords(Map<String, String> map);

}
