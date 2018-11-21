package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class IdTypeRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -413796195850519145L;

	private List<Id> idtypes;
}
