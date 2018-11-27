package io.mosip.kernel.core.idvalidator.spi;

/**
 * This interface provide method for RID validation.
 * 
 * @author Ritesh Sinha
 *
 * @param <T>
 *            is of custom type
 */
public interface RidValidator<T> {
	/**
	 * This method validate given RID against specified generation logic.
	 * 
	 * @param id
	 *            the RID.
	 * @param centerId
	 *            the centerId.
	 * @param dongleId
	 *            the dongleId.
	 * @return true if RID satisfied the specified generation logic else false.
	 */
	boolean validateId(T id, T centerId, T dongleId);
}
