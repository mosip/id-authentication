package io.mosip.kernel.masterdata.dto.getresponse;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kishan Rathore
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekDaysDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3763349609534307197L;
	
	private String name;
	
	private short order;
	
	private String languageCode;

}
