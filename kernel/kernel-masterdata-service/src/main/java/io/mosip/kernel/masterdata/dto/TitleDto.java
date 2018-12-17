package io.mosip.kernel.masterdata.dto;

import lombok.Data;

/**
 * DTO class for fetching titles from masterdata
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Data


public class TitleDto {
	private String code;
	private String titleName;
	private String titleDescription;
	private Boolean isActive;

}
