package io.mosip.registration.dto.demographic;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class used to capture the Address of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class AddressDTO extends BaseDTO {

	protected String line1;
	protected String line2;
	protected String line3;
	protected LocationDTO locationDTO;

	/**
	 * @return the line1
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @param line1
	 *            the line1 to set
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}

	/**
	 * @return the line2
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @param line2
	 *            the line2 to set
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}

	/**
	 * @return the line3
	 */
	public String getLine3() {
		return line3;
	}

	/**
	 * @param line3
	 *            the line3 to set
	 */
	public void setLine3(String line3) {
		this.line3 = line3;
	}

	/**
	 * @return the locationDTO
	 */
	public LocationDTO getLocationDTO() {
		return locationDTO;
	}

	/**
	 * @param locationDTO
	 *            the locationDTO to set
	 */
	public void setLocationDTO(LocationDTO locationDTO) {
		this.locationDTO = locationDTO;
	}

}
