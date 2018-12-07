package io.mosip.kernel.masterdata.dto.getresponse;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data


public class LocationHierarchyDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4552110961570300174L;
	
	@NotNull
	private Short locationHierarchylevel;
	
	@NotNull
	@Size(min=1,max=64)
	private String locationHierarchyName;
	
	@NotNull
	private Boolean isActive;

}
