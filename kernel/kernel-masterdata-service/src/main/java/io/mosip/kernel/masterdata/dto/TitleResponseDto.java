package io.mosip.kernel.synchandler.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto class for fetching response from master data
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitleResponseDto {

	private List<TitleDto> titleList;

}
