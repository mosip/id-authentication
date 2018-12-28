package io.mosip.preregistration.booking.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreRegIdsByRegCenterIdDTO implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8012017032440020062L;
	private String registration_center_id;
	private List<String> pre_registration_ids;
}
