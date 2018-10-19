package io.mosip.registration.processor.core.packet.dto;

public class AddressDTO {

	private String line1;

	private String line2;

	private String line3;

	private LocationDTO locationDTO;


	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public LocationDTO getLocation() {
		return locationDTO;
	}

	public void setLocation(LocationDTO location) {
		this.locationDTO = locationDTO;
	}


	

}