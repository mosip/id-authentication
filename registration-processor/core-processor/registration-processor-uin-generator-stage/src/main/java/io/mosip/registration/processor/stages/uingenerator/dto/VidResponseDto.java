package io.mosip.registration.processor.stages.uingenerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel(description = "Model representing a Vid Reponse")
public class VidResponseDto {
	
	private String vidStatus;
	
	@JsonProperty("VID")
	private String VID;
	
	private String restoredVid;
	
	
	@JsonProperty("UIN")
	private String UIN;
	

}