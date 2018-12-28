package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PreRegIdsByRegCenterIdResponseDTO implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8225498964365164064L;
	private String registration_center_id;
	private List<String> pre_registration_ids;
}
