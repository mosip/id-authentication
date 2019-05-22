package io.mosip.kernel.core.bioapi.model;

import java.util.Date;

import lombok.Data;

/**
 * The Class BIRInfo.
 * 
 * @author Sanjay Murali
 */
@Data
public class BIRInfo {
	private String creator ;
	private String index ; // UUID with pattern
	private String payload ;
	private Boolean integrity ;
	private Date creationDate ;
	private Date notValidBefore ;
	private Date notValidAfter ;
}
