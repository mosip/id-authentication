package io.mosip.registration.dto;

import javafx.beans.property.SimpleStringProperty;

/**
 * The Class RegistrationApprovalDTO.
 * 
 * @author Mahesh Kumar
 */
public class RegistrationApprovalDTO {

	private SimpleStringProperty id;
	private SimpleStringProperty acknowledgementFormPath;
	private SimpleStringProperty statusComment;

	/**
	 * Instantiates a new registration approval DTO.
	 *
	 * @param id 
	 * 				the id
	 * @param acknowledgementFormPath 
	 * 				the acknowledgement form path
	 * @param statusComment 
	 * 				the status comment
	 */
	public RegistrationApprovalDTO(String id, String acknowledgementFormPath, String statusComment) {
		super();
		this.id = new SimpleStringProperty(id);
		this.acknowledgementFormPath = new SimpleStringProperty(acknowledgementFormPath);
		this.statusComment = new SimpleStringProperty(statusComment);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id.get();
	}

	/**
	 * Gets the acknowledgement form path.
	 *
	 * @return the acknowledgementFormPath
	 */
	public String getAcknowledgementFormPath() {
		return acknowledgementFormPath.get();
	}

	/**
	 * Gets the status comment.
	 *
	 * @return the statusComment
	 */
	public String getStatusComment() {
		return statusComment.get();
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = new SimpleStringProperty(id);
	}

	/**
	 * @param acknowledgementFormPath the acknowledgementFormPath to set
	 */
	public void setAcknowledgementFormPath(String acknowledgementFormPath) {
		this.acknowledgementFormPath = new SimpleStringProperty(acknowledgementFormPath);
	}

	/**
	 * @param statusComment the statusComment to set
	 */
	public void setStatusComment(String statusComment) {
		this.statusComment = new SimpleStringProperty(statusComment);
	}
}
