package io.mosip.registration.dto.demographic;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class used to capture the Address of the Individual
 * 
 * @author Dinesh Asokan
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class AddressDTO extends BaseDTO {

	protected String addressLine1;
	protected String addressLine2;
	protected String addressLine3;
	protected LocationDTO locationDTO;

	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return addressLine1;
	}

	/**
	 * @param addressLine1
	 *            the addressLine1 to set
	 */
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * @return the addressLine2
	 */
	public String getAddressLine2() {
		return addressLine2;
	}

	/**
	 * @param addressLine2
	 *            the addressLine2 to set
	 */
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @return the addressLine3
	 */
	public String getAddressLine3() {
		return addressLine3;
	}

	/**
	 * @param addressLine3
	 *            the addressLine3 to set
	 */
	public void setLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
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
