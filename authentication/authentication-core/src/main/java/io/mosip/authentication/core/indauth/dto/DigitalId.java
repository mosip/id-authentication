package io.mosip.authentication.core.indauth.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Manoj SP
 *
 */
@Data
public class DigitalId {
	
	private String serialNo;
	
	private String make;
	
	private String model;
	
	private String type;
	
	private String deviceSubType;
	
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private String deviceProvider;
	
	@Setter(AccessLevel.NONE)
	private String dp;
	
	@Setter(AccessLevel.NONE)
	private String dpId;
	
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private String deviceProviderId;
	
	private String dateTime;
	
	public void setDeviceProvider(String deviceProvider) {
		this.deviceProvider = deviceProvider;
		this.dp = deviceProvider;
	}
	
	public void setDeviceProviderId(String deviceProviderId) {
		this.deviceProviderId = deviceProviderId;
		this.dpId = deviceProviderId;
	}
}
