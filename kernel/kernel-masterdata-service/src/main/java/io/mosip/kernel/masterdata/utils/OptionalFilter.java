package io.mosip.kernel.masterdata.utils;

import java.util.List;

import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Optional Filters
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionalFilter {
	List<SearchFilter> filters;
}
