package io.mosip.kernel.core.bioapi.model;

import lombok.Data;

/**
 * The Class CompositeScore.
 * 
 * @author Sanjay Murali
 */
@Data
public class CompositeScore {
	private float scaledScore ; //0 - 100, used for internal classification and	efficacy analysis
	private long internalScore ; // used against threshold specified in config file
	private Score [] individualScores ; // List of score for individual matches.
	//Array size matches the input sample array size.
	private KeyValuePair [] analyticsInfo ; // detailed breakdown and other	information
}
