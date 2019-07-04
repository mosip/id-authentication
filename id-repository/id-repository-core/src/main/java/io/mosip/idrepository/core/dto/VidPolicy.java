package io.mosip.idrepository.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * The Class VidPolicy - POJO class that provides details about vid policy.
 *
 * @author Manoj SP
 */
@Data
public class VidPolicy {

	/** The valid for in minutes. */
	private Integer validForInMinutes;
	
	/** The allowed transactions. */
	@JsonProperty("transactionsAllowed")
	private Integer allowedTransactions;
	
	/** The allowed instances. */
	@JsonProperty("instancesAllowed")
	private Integer allowedInstances;
	
	/** The auto restore allowed. */
	private Boolean autoRestoreAllowed;
	
	/** The restore on action. */
	private String restoreOnAction;
}
