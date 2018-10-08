package io.mosip.kernel.datamapper.orika.fieldmapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcludeDataField {

	private String sourceField;
	private boolean mapExcludeFieldNull;
}
