package io.mosip.kernel.masterdata.dto.request;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto to hold the request page info
 * 
 * @author Abhishek Kumar
 * @since 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {

	@Min(value = 0)
	private int pageStart;

	@Min(value = 1)
	private int pageFetch = 20;

}
