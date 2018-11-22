package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * DTO class for holding the idtypes list.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class IdTypeListDto implements Serializable {
	/**
	 * Serializable version id.
	 */
	private static final long serialVersionUID = -413796195850519145L;

	/**
	 * The list of id types.
	 */
	private List<IdTypeRequest> idtypes;
}
