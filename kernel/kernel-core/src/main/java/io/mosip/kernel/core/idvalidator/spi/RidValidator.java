package io.mosip.kernel.core.idvalidator.spi;

/**
 * This interface provide method for RID validation.
 * 
 * @author Ritesh Sinha
 * @author Abhishek Kumar
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
	 * @param machineId
	 *            the dongleId.
	 * @return true if RID satisfied the specified generation logic.
	 */
	boolean validateId(T id, T centerId, T machineId);

	/**
	 * This method validate given RID only against specified generation logic.
	 * 
	 * @param id
	 *            the RID.
	 * @return true if RID satisfied the specified generation logic.
	 */
	boolean validateId(T id);

	/**
	 * This method validate given RID against specified generation logic.
	 * 
	 * @param id
	 *            the rid
	 * @param centerId
	 *            the center id
	 * @param machineId
	 *            the machine id
	 * @param centerIdLength
	 *            the center id length
	 * @param machineIdLength
	 *            the machine id length
	 * @param sequenceLength
	 *            length of the sequence
	 * @param timeStampLength
	 *            timeStamp length
	 * @return true if RID satisfied the specified generation logic.
	 */
	boolean validateId(T id, T centerId, T machineId, int centerIdLength, int machineIdLength, int sequenceLength,
			int timeStampLength);

	/**
	 * This method validate given RID against specified generation logic.
	 * 
	 * @param id
	 *            the rid
	 * @param centerIdLength
	 *            the center id length
	 * @param machineIdLength
	 *            the machine id length
	 * @param sequenceLength
	 *            length of the sequence
	 * @param timeStampLength
	 *            timeStamp length
	 * @return true if RID satisfied the specified generation logic.
	 */
	public boolean validateId(T id, int centerIdLength, int machineIdLength, int sequenceLength,
			int timeStampLength);
}
