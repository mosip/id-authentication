package io.mosip.kernel.masterdata.dto.getresponse;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data


public class LocationHierarchyResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8111451245460042722L;
	
	private List<LocationHierarchyDto> locations;

}
