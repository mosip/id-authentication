package io.mosip.kernel.synchandler.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class work as a request object which will be provided by the user or
 * given by the MOSIP system respectively.
 * 
 * @author Bal Vikash Sharma
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class LanguageResponseDto {

	/**
	 * List of Languages.
	 */
	private List<LanguageDto> languages;

}
