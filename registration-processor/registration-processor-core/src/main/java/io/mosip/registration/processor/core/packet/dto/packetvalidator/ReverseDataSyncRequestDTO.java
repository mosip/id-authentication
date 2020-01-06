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
	
	public Date getUpdateDateTime() {
		if(updateDateTime!=null)
			return new Date(updateDateTime.getTime());
		return null;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime=updateDateTime!=null?new Date(updateDateTime.getTime()):null;
	}
	
	public Date getCreatedDateTime() {
		if(createdDateTime!=null)
			return new Date(createdDateTime.getTime());
		return null;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime=createdDateTime!=null?new Date(createdDateTime.getTime()):null;
	}

}
