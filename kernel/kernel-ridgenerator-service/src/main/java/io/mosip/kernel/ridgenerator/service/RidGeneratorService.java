package io.mosip.kernel.ridgenerator.service;

/**
 * This interface contains methods for RID generation.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 * @param <T>
 *            the requestDTO.
 */
public interface RidGeneratorService<T> {

	/**
	 * This method generate RID based on center id and machine id provided.
	 * 
	 * @param centerId
	 *            the center id.
	 * @param machineId
	 *            the machine id.
	 * @return the generated RID.
	 */
	public T generateRid(String centerId, String machineId);
}
