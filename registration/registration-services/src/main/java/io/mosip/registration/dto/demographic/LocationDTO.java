/**
 * 
 */
package io.mosip.registration.dto.demographic;

import io.mosip.registration.dto.BaseDTO;

/**
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class LocationDTO extends BaseDTO {

	protected String region;
	protected String city;
	protected String province;
	protected String localAdministrativeAuthority;
	protected String postalCode;

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province
	 *            the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the localAdministrativeAuthority
	 */
	public String getLocalAdministrativeAuthority() {
		return localAdministrativeAuthority;
	}

	/**
	 * @param localAdministrativeAuthority the localAdministrativeAuthority to set
	 */
	public void setLocalAdministrativeAuthority(String localAdministrativeAuthority) {
		this.localAdministrativeAuthority = localAdministrativeAuthority;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode
	 *            the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

}
