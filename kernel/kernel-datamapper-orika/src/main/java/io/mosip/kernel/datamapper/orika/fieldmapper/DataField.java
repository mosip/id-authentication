package io.mosip.kernel.datamapper.orika.fieldmapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataField {
	
	private String sourceField;
	private String destinationField;
	
}
