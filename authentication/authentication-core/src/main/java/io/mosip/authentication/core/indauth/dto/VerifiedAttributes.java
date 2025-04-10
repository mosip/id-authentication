package io.mosip.authentication.core.indauth.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * General-purpose of {@code VerifiedAttributes} class used to provide Verified 
 * Attributes.
 * 
 * @author Mahammed Taheer
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifiedAttributes {

	/** Variable to hold trustFramework */
	private String trustFramework;

	/** Variable to hold time */
	private LocalDateTime time;

	/** Variable to hold trustFramework */
	private String verificationProcess;
}
