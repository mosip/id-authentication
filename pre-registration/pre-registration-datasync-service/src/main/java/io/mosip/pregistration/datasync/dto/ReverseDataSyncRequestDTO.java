package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ReverseDataSyncRequestDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2626266155604751029L;
	private List<String> pre_registration_ids;
}
