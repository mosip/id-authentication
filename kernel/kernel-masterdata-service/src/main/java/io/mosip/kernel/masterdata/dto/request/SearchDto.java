package io.mosip.kernel.masterdata.dto.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto to hold the search criteria.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {

	@NotNull
	@Valid
	private List<SearchFilter> filters;

	@NotNull
	private List<SearchSort> sort;

	@NotNull
	private Pagination pagination;

	@ValidLangCode(message="Language Code is Invalid")
	private String languageCode;
}
