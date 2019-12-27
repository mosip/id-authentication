package io.mosip.resident.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.ToString;

@ToString
@ApiModel(description = "Model representing a Vid Request")
public class VidGeneratorRequestDto {

	private String vidType;

	private String UIN;

	public String getVidType() {
		return vidType;
	}

	public void setVidType(String vidType) {
		this.vidType = vidType;
	}

	@JsonProperty("UIN")
	public String getUIN() {
		return UIN;
	}

	public void setUIN(String uIN) {
		UIN = uIN;
	}

	public String getVidStatus() {
		return vidStatus;
	}

	public void setVidStatus(String vidStatus) {
		this.vidStatus = vidStatus;
	}

	private String vidStatus;

}
