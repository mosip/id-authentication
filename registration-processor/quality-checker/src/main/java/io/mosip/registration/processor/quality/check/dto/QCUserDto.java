package io.mosip.registration.processor.quality.check.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class QCUserDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String qcUserId;
	private String regId;
	private String decisionStatus;


}
