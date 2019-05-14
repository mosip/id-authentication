package io.mosip.idrepository.vid.dto;

import lombok.Data;

@Data
public class VidPolicy {

	private String validForInMinutes;
	
	private String allowedTransactions;
	
	private String allowedInstances;
	
	private String autoRestoreAllowed;
	
	private String restoreOnAction;
}
