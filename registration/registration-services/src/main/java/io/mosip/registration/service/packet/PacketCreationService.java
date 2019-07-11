package io.mosip.registration.service.packet;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to create the Individual Registration packet.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface PacketCreationService {

	/**
	 * Creates the Registration Packet.
	 * 
	 * <p>
	 * The in-memory zip file will be created out of the {@link RegistrationDTO}.
	 * </p>
	 * 
	 * <p>
	 * This method internally does the following:
	 * </p>
	 * <ol>
	 * <li>Validate the ID Object against</li>
	 * <li>Create CBEFF for Individual, Introducer, Officer and Supervisor based on
	 * biometrics provided</li>
	 * <li>Fetch Audits from database</li>
	 * <li>Generate Packet Data Hash</li>
	 * <li>Generate OSI Hash</li>
	 * <li>Generate Packet Meta Info</li>
	 * <li>Fetch the available devices</li>
	 * <li>Generate the in-memory zip file
	 * <li>
	 * </ol>
	 * 
	 * <p>
	 * Returns the byte array of the generated in-memory zip file
	 * </p>
	 * 
	 * @param registrationDTO
	 *            the enrollment data for which packet has to be created
	 * @throws RegBaseCheckedException
	 *             the checked exception
	 * @return the in-memory zip file as bytes
	 */
	byte[] create(RegistrationDTO registrationDTO) throws RegBaseCheckedException;
}
