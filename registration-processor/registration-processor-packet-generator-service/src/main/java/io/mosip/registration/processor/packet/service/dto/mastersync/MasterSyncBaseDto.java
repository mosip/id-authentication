package io.mosip.registration.processor.packet.service.dto.mastersync;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public class MasterSyncBaseDto {
	
	private Boolean isDeleted;

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
