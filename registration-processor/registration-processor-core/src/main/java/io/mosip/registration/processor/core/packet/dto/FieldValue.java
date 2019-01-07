package io.mosip.registration.processor.core.packet.dto;
	
/**
 * This class contains the attributes to be displayed for flat value object in
 * PacketMetaInfo JSON.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class FieldValue {

	/** The label. */
	private String label;
	
	/** The value. */
	private String value;

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
	 * @param label            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
