package io.mosip.registration.controller.vo;

import javafx.beans.property.SimpleStringProperty;

/**
 * The Class ExceptionListVO.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
public class ExceptionListVO {
	
	private SimpleStringProperty exceptionItem;
	
	/**
	 * Instantiates a new exception list VO.
	 *
	 * @param exceptionItem   the exception Item
	 */
	public ExceptionListVO(String exceptionItem) {
		super();
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
		this.exceptionItem = new SimpleStringProperty(exceptionItem);
	}

}
