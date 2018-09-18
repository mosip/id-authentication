package org.mosip.kernel.core.spi.idgenerator;

/**
 * This is an interface for the generation of EID
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public interface MosipEidGenerator {
	/**
	 * This method is used to generate Enrollment ID(EID)
	 * 
	 * @param agentId
	 *            input given by user
	 * @param machineId
	 *            input by user
	 * @return string containing generated EID
	 */
	public String eidGeneration(String agentId, String machineId);

}
