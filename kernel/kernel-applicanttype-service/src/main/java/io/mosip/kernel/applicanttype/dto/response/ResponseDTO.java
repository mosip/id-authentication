package io.mosip.kernel.applicanttype.dto.response;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 
 * @author Bal Vikash Sharma
 *
 */
@Data
public class ResponseDTO {

	@NotNull
	private ApplicantTypeCodeDTO applicantType;

}