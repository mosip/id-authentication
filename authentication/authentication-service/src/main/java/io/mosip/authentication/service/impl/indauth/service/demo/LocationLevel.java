package io.mosip.authentication.service.impl.indauth.service.demo;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public enum LocationLevel {
	COUNTRY(0, "COUNTRY"), STATE(1, "STATE"), CITY(2, "CITY"), AREA(3, "AREA"), ZIPCODE(4, "ZIPCODE");

	private int code;
	private String name;

	private LocationLevel(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
