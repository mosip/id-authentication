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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime == null ? createdDateTime : new Date(createdDateTime.getTime());
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime == null ? updateDateTime : new Date(updateDateTime.getTime());
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public List<String> getPreRegistrationIds() {
		return preRegistrationIds;
	}

	public void setPreRegistrationIds(List<String> preRegistrationIds) {
		this.preRegistrationIds = preRegistrationIds;
	}
	
	
}
