package io.mosip.kernel.core.bioapi.model;

import lombok.Data;

/**
 * The Class QualityScore.
 * 
 * @author Sanjay Murali
 */
@Data
public class QualityScore {
	private long internalScore ; // used against threshold specified in config file
}
