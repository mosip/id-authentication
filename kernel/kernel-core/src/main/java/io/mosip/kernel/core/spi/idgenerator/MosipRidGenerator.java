package io.mosip.kernel.core.spi.idgenerator;

/**
 * This is an interface for the generation of RID
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public interface MosipRidGenerator<T> {
	/**
	 * This method is used to generate Registration ID(RID)
	 * 
	 * @param agentId
	 *            input given by user
	 * @param machineId
	 *            input by user
	 * @return string containing generated RID
	 */
	public T generateId(String agentId, String machineId);

}
