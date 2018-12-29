package io.mosip.preregistration.datasync.dto;

import java.io.Serializable;
import java.util.Date;
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
	 * Created By
	 */
	private String createdBy;
	/**
	 * Created date time
	 */
	private Date createdDateTime;
	/**
	 * Update By
	 */
	private String updateBy;
	/**
	 * Update date time
	 */
	private Date updateDateTime;
	/**
	 * language code
	 */
	private String langCode;
	
	/**
	 * List to store the list of pre Ids
	 */
	private List<String> preRegistrationIds;
}
