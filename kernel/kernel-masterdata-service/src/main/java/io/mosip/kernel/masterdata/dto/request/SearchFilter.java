package io.mosip.kernel.masterdata.dto.request;

import javax.validation.constraints.NotBlank;

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
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilter {

	@NotBlank
	private String columnName;

	@NotBlank
	private String type;

	private String value;

	private String fromValue;

	private String toValue;

}
