package io.mosip.registration.dto;

import javafx.beans.property.SimpleStringProperty;

public class ExceptionListDTO {
	
	private SimpleStringProperty exceptionItem;
	
	public ExceptionListDTO(String exceptionItem) {
		this.exceptionItem = new SimpleStringProperty(exceptionItem);
	}

	/**
	 * @return the exceptionItem
	 */
	public String getExceptionItem() {
		return exceptionItem.get();
	}

	/**
	 * @param exceptionItem the exceptionItem to set
	 */
	public void setExceptionItem(String exceptionItem) {
		this.exceptionItem.set(exceptionItem);
	}
}
