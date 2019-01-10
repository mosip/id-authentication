package io.mosip.registration.dto;

import javafx.beans.property.SimpleStringProperty;

public class RegistrationApprovalDTO {

	private SimpleStringProperty id;
	private SimpleStringProperty acknowledgementFormPath;
	private SimpleStringProperty statusComment;

	/**
	 * @param id
	 * @param type
	 * @param name
	 * @param operatorId
	 * @param operatorName
	 */
	public RegistrationApprovalDTO(String id, String acknowledgementFormPath, String statusComment) {
		super();
		this.id = new SimpleStringProperty(id);
		this.acknowledgementFormPath = new SimpleStringProperty(acknowledgementFormPath);
		this.statusComment = new SimpleStringProperty(statusComment);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id.get();
	}

	/**
	 * @return the acknowledgementFormPath
	 */
	public String getAcknowledgementFormPath() {
		return acknowledgementFormPath.get();
	}

	/**
	 * @return the statusComment
	 */
	public String getStatusComment() {
		return statusComment.get();
	}
}
