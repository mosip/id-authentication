package io.mosip.kernel.synchandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for fetching titles from masterdata
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TitleDto {
	private String titleCode;
	private String titleName;
	private String titleDescription;
	private Boolean isActive;

}
