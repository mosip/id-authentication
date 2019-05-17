package io.mosip.idrepository.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VidPolicy {

	private Integer validForInMinutes;
	
	@JsonProperty("transactionsAllowed")
	private Integer allowedTransactions;
	
	@JsonProperty("instancesAllowed")
	private Integer allowedInstances;
	
	private Boolean autoRestoreAllowed;
	
	private String restoreOnAction;
}
