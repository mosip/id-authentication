package io.mosip.authentication.core.dto;

/**
 * The Interface ObjectWIthIdVersion.
 * @author Loganathan S
 */
public interface ObjectWithIdVersionTransactionID {

	String getId();
	
	void setId(String id);
	
	String getVersion();
	
	void setVersion(String version);
	
	String getTransactionID();
	
	void setTransactionID(String transactionID);
}
