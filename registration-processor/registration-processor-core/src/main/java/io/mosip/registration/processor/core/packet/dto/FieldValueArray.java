package io.mosip.registration.processor.core.packet.dto;
	
import java.util.List;

/**
 * This class contains the attributes to be displayed for flat array object in
 * PacketMetaInfo JSON.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class FieldValueArray {

	/** The label. */
	private String label;
	
	/** The value. */
	private List<String> value;

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
	public List<String> getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value            the value to set
	 */
	public void setValue(List<String> value) {
		this.value = value;
	}

}
