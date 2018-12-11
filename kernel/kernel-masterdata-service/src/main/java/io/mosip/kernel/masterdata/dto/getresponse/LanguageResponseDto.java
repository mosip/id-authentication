package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.mosip.kernel.masterdata.dto.LanguageDto;
import lombok.Data;

/**
 * This class work as a request object which will be provided by the user or
 * given by the MOSIP system respectively.
 * 
 * @author Bal Vikash Sharma
 * @Version 1.0.0
 */
@Data


@JsonInclude(Include.NON_NULL)
public class LanguageResponseDto {

	/**
	 * List of Languages.
	 */
	private List<LanguageDto> languages;

}
