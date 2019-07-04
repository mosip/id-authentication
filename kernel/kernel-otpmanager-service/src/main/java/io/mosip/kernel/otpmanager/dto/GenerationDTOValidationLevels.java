package io.mosip.kernel.otpmanager.dto;

import javax.validation.GroupSequence;

/**
 * This class contains group sequence levels for validation of key.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public class GenerationDTOValidationLevels {
	@GroupSequence({ NullEmptyValidationLevel.class, InvalidLengthValidationLevel.class })
	public interface ValidationLevel {

	}

	public interface NullEmptyValidationLevel {
	}

	public interface InvalidLengthValidationLevel {
	}
}
