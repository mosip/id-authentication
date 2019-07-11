package io.mosip.registration.service.external;

import java.util.Base64;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to store the encrypted packet of the {@link Registration} in
 * configured location in local disk
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface StorageService {

	/**
	 * The packet will be stored to the configured location in the local system.
	 * 
	 * <p>
	 * The input encrypted registration packet will be {@link Base64} encoded using
	 * {@link CryptoUtil} and then the encoded packet will be stored to the
	 * configured local storage under a folder. The name of the folder will be
	 * current date in configured format.
	 * </p>
	 * 
	 * <p>
	 * The configured storage location and date pattern will be taken form the
	 * global params using {@link ApplicationContext} map.
	 * </p>
	 * 
	 * <p>
	 * Returns the location where packet had been stored.
	 * </p>
	 * 
	 * @param registrationId
	 *            the id of the {@link Registration}
	 * @param packet
	 *            the encrypted packet data to be stored in local storage
	 * @return the file path where the files had been stored
	 * @throws RegBaseCheckedException
	 *             any exception while saving the encrypted packet
	 */
	String storeToDisk(String registrationId, byte[] packet) throws RegBaseCheckedException;
}
