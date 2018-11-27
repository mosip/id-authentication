package io.mosip.registration.dto;

import javafx.beans.property.SimpleStringProperty;

public class RegistrationApprovalDTO {

	private SimpleStringProperty id;
	private SimpleStringProperty acknowledgementFormPath;

	/**
	 * @param id
	 * @param type
	 * @param name
	 * @param operatorId
	 * @param operatorName
	 */
	public RegistrationApprovalDTO(String id, String acknowledgementFormPath) {
		super();
		this.id = new SimpleStringProperty(id);
		this.acknowledgementFormPath = new SimpleStringProperty(acknowledgementFormPath);
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
}
