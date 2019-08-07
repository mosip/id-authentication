package io.mosip.kernel.masterdata.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dtp to hold the search parameters
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */

@Data
@AllArgsConstructor()
@NoArgsConstructor
public class SearchFilter {
	private String value;

	//@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$", message = "Invalid date time pattern")
	private String fromValue;

	//@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$", message = "Invalid date time pattern")
	private String toValue;

	@NotBlank
	private String columnName;

	@NotNull
	private String type;

}
