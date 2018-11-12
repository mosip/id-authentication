package io.mosip.registration.service;

import java.util.List;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface for creating the seed values to generate the AES Session Key
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface AESSeedGenerator {

	/**
	 * Generates the {@link List} containing the seeds for AES Session Key
	 * Generation
	 * 
	 * @return {@link List} Contains the seeds for AES Session Key
	 *         Generation
	 * @throws RegBaseCheckedException
	 */
	List<String> generateAESKeySeeds() throws RegBaseCheckedException;
}
