package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Id implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5474187315520185266L;
	private String code;
	private String langCode;
	private String name;
	private String descr;

}
