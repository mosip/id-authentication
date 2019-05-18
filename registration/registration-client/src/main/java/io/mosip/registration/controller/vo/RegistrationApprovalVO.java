package io.mosip.registration.controller.vo;

import javafx.beans.property.SimpleStringProperty;

/**
 * The Class RegistrationApprovalVO.
 * 
 * @author Mahesh Kumar
 */
public class RegistrationApprovalVO {

	private SimpleStringProperty id;
	private SimpleStringProperty acknowledgementFormPath;
	private SimpleStringProperty statusComment;

	/**
	 * Instantiates a new registration approval VO.
	 *
	 * @param id                      the id
	 * @param acknowledgementFormPath the acknowledgement form path
	 * @param statusComment           the status comment
	 */
	public RegistrationApprovalVO(String id, String acknowledgementFormPath, String statusComment) {
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
}
