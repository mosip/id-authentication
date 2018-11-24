package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author M1046129
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class ReverseDataSyncRequestDTO implements Serializable {
	
	/**
	 * auto generated serialVersionUID
	 */
	private static final long serialVersionUID = -2626266155604751029L;
	
	
	/**
	 * List to store the list of pre Ids
	 */
	private List<String> pre_registration_ids;
}
