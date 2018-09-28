package org.mosip.registration.dto;

import javafx.beans.property.SimpleStringProperty;

public class RegistrationApprovalUiDto {

	private  Boolean expand;
	private  SimpleStringProperty id;
	private  SimpleStringProperty type;
	private  SimpleStringProperty name;
	private  SimpleStringProperty operatorId;
	private  SimpleStringProperty operatorName;
	private  SimpleStringProperty acknowledgementFormPath;
	/**
	 * @param id
	 * @param type
	 * @param name
	 * @param operatorId
	 * @param operatorName
	 */
	public RegistrationApprovalUiDto(String id, String type, String name, String operatorId, String operatorName, String acknowledgementFormPath) {
		super();
		expand=new Boolean(true);
		this.id = new SimpleStringProperty(id);
		this.type = new SimpleStringProperty(type);
		this.name = new SimpleStringProperty(name);
		this.operatorId = new SimpleStringProperty(operatorId);
		this.operatorName = new SimpleStringProperty(operatorName);
		this.acknowledgementFormPath = new SimpleStringProperty(acknowledgementFormPath);
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id.get();
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type.get();
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name.get();
	}
	/**
	 * @return the operatorId
	 */
	public String getOperatorId() {
		return operatorId.get();
	}
	/**
	 * @return the operatorName
	 */
	public String getOperatorName() {
		return operatorName.get();
	}
	
	public Boolean getExpand() {
		return expand;
	}
	/**
	 * @return the acknowledgementFormPath
	 */
	public String getAcknowledgementFormPath() {
		return acknowledgementFormPath.get();
	}
}
