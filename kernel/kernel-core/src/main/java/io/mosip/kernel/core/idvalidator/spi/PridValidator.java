package io.mosip.kernel.core.idvalidator.spi;

/**
 * This interface provide method for PRID validation.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
public interface PridValidator<T> {
	/**
	 * Function to validate given Id
	 * 
	 * @param id
	 *            The Id to validate
	 * @return true if Id is valid
	 */
	boolean validateId(T id);

	/**
	 * Function to validate given Id
	 * 
	 * @param id
	 *            the id to validate
	 * @param pridLength
	 *            prid length to validate
	 * @param sequenceLimit
	 *            sequence in prid to limit
	 * @param repeatingLimit
	 *            repeating limit
	 * @param blockLimit
	 *            repeating block limit
	 * @return true if Id is valid
	 */
	boolean validateId(String id, int pridLength, int sequenceLimit, int repeatingLimit, int blockLimit);
}
