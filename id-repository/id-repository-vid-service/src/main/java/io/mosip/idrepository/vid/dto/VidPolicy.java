package io.mosip.idrepository.vid.dto;

import lombok.Data;

@Data
public class VidPolicy {

	private Integer validForInMinutes;
	
	private Integer allowedTransactions;
	
	private Integer allowedInstances;
	
	private Boolean autoRestoreAllowed;
	
	private String restoreOnAction;
}
