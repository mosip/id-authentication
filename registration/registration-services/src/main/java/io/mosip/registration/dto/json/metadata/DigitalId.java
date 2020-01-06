package io.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class DigitalId {

	private String serialNo;
	private String make;
	private String model;
	private String type;
	private String dpId;
	private String dp;
	private String subType;
	private String dateTime;
}
