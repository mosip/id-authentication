package io.mosip.registration.dto.json.metadata;

/**
 * This class is to capture the json parsing geolocation data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class GeoLocation {
	private double latitude;
	private double longitude;

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
