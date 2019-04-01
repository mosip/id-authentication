package io.mosip.kernel.applicanttype.dto.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.mosip.kernel.applicanttype.dto.KeyValues;
import lombok.Data;

/**
 * 
 * @author Bal Vikash Sharma
 *
 */
@Data
public class RequestDTO {
	@NotNull
	private List<KeyValues<String, Object>> attributes;
}