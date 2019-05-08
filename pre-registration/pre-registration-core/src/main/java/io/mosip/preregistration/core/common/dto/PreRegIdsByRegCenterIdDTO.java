package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PreRegIdsByRegCenterIdDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8012017032440020062L;
	/**
	 * registration Center Id
	 */
	@JsonProperty("registartion_center_id")
	@ApiModelProperty(value = "Registartion Center ID", position = 1)
	private String registrationCenterId;
	
	
	/**
	 * pre-registration id
	 */
	@JsonProperty("pre_registration_ids")
	@ApiModelProperty(value = "List of Pre-Registartion IDs", position = 2)
	private List<String> preRegistrationIds;
}
