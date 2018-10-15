package io.mosip.kernel.datamapper.orika.fieldmapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This Class contains the source class 
 * field names that needs to be excluded.
 * 
 * @author Neha
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcludeDataField {

	private String sourceField;
	
	/**
	 * Field to Configure whether to map nulls in generated 
	 * mapper code of excluded fields
	 */
	private boolean mapExcludeFieldNull;
}
