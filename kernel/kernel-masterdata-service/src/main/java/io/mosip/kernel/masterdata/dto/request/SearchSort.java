package io.mosip.kernel.masterdata.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto to hold the Sort criteria.
 * 
 * @author Abhishek Kumar
 * @since 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchSort {

	private String sortField;

	private String sortType;
}
