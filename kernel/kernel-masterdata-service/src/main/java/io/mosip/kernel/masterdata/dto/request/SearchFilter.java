package io.mosip.kernel.masterdata.dto.request;

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
@EqualsAndHashCode(callSuper = true)
public class SearchFilter extends FilterDto {

	private String value;

	private String fromValue;

	private String toValue;

}
