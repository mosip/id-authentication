package io.mosip.authentication.core.dto.indauth;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic for bio authentication.
 *
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BioDTO {

	@NotNull
	private BioType type;

	@NotNull
	private String bioData;

}
