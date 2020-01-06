package io.mosip.kernel.core.bioapi.model;

import lombok.Data;

/**
 * The Class Score.
 * 
 * @author Sanjay Murali
 */
@Data
public class Score {
	private float scaleScore ; //0 - 100, used for internal classification and	efficacy analysis
	private long internalScore ; // used against threshold specified in config file
	private KeyValuePair [] analyticsInfo ; // detailed breakdown and other	information
}
