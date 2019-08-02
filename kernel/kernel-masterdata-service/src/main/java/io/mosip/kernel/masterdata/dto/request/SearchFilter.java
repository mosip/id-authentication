package io.mosip.kernel.masterdata.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

	private String fromValue;

	private String toValue;
	
	@NotBlank
	private String columnName;

	@NotNull
	private String type;

}
