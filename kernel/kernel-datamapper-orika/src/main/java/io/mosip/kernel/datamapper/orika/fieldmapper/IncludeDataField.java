package io.mosip.kernel.datamapper.orika.fieldmapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * This Class contains the source and destination classes 
 * field names that needs to be included.
 * 
 * @author Neha
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncludeDataField {
	
	private String sourceField;
	private String destinationField;
	
	/**
	 * Field to Configure whether to map nulls in generated 
	 * mapper code of included fields
	 */
	private boolean mapIncludeFieldNull;

}
