package io.mosip.preregistration.core.common.dto.identity;

import java.io.Serializable;

import lombok.Data;

@Data
public class IdentityJsonValues implements Serializable{
	/**
	 * constant serialVersion UID
	 */
	private static final long serialVersionUID = 8450727654084571180L;

	/** The value. */
	private String value;

	/* Mandatory check */
	private Boolean isMandatory;
}
