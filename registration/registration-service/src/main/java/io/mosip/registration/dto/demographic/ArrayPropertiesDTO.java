package io.mosip.registration.dto.demographic;

import java.util.LinkedList;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the label of the field and its values in configured
 * languages.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class ArrayPropertiesDTO extends BaseDTO {

	/** The label. */
	private String label;

	/** The values. */
	private LinkedList<ValuesDTO> values;

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 *
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public LinkedList<ValuesDTO> getValues() {
		return values;
	}

	/**
	 * Sets the values.
	 *
	 * @param values
	 *            the values to set
	 */
	public void setValues(LinkedList<ValuesDTO> values) {
		this.values = values;
	}
}
