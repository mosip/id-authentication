package io.mosip.registration.processor.core.packet.dto.packetvalidator;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author Girish Yarru
 *
 */
@Data
@ToString
public class ReverseDataSyncRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4636516358762112318L;

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
