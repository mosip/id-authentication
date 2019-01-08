package io.mosip.registration.processor.core.spi.biodedupe;

import java.util.List;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;

/**
 * The Interface BioDedupeService.
 */
public interface BioDedupeService {

	/**
	 * Insert biometrics.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the string
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	public String insertBiometrics(String registrationId) throws ApisResourceAccessException;

	/**
	 * Perform dedupe.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the list
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	public List<String> performDedupe(String registrationId) throws ApisResourceAccessException;

	/**
	 * Gets the file.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the file
	 */
	public byte[] getFile(String registrationId);

}
