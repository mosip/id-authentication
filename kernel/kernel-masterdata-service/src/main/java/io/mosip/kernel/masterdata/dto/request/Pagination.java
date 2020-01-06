package io.mosip.kernel.masterdata.dto.request;

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

	private int pageStart;

	private int pageFetch = 10;

}
