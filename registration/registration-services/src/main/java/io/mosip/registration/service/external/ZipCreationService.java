package io.mosip.registration.service.external;

import java.util.Map;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to generate the in-memory zip file for Registration Packet
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface ZipCreationService {

	/**
	 * Creates an in-memory zip file out of {@link RegistrationDTO} and the input
	 * {@link Map} and returns the zip file as byte array.
	 * 
	 * <p>
	 * The input {@link Map} contains the following files:
	 * </p>
	 * <ul>
	 * <li>CBEFF files of
	 * <ul>
	 * <li>Individual</li>
	 * <li>Introducer</li>
	 * <li>Officer</li>
	 * <li>Supervisor</li>
	 * </ul>
	 * </li>
	 * <li>Exception Photo of Individual and Introducer</li>
	 * <li>Audits JSON</li>
	 * <li>Packet Hash Data</li>
	 * <li>OSI Hash Data</li>
	 * <li>Packet Meta Info JSON</li>
	 * </ul>
	 *
	 * <p>
	 * The following files will be retrived from the input {@link RegistrationDTO}:
	 * </p>
	 * <ul>
	 * <li>Identity JSON</li>
	 * <li>Proof Documents</li>
	 * </ul>
	 * 
	 * <p>
	 * Returns the byte array of the generated in-memory zip file.
	 * </p>
	 * 
	 * @param registrationDTO
	 *            the Registration to be stored in zip file
	 * @param jsonMap
	 *            contains the map of files to be zipped
	 * @return the byte array of packet zip file
	 * @throws RegBaseCheckedException
	 *             if any error occurs while zipping the files
	 */
	byte[] createPacket(final RegistrationDTO registrationDTO, final Map<String, byte[]> jsonMap)
			throws RegBaseCheckedException;
}
