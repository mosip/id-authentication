package io.mosip.preregistration.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * View registration response DTO
 * 
 * @author M1037717
 *
 */


@Getter
@Setter
@NoArgsConstructor
@ToString
public class ViewRegistrationResponseDto {

	private String upd_dtimesz;
	private String group_id;
	private String status_code;
	private String firstname;
	private int noOfRecords;
}
