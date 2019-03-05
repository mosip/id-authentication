package io.mosip.registration.processor.service.external;

import io.mosip.registration.processor.packet.service.dto.PreRegistrationDTO;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;

public interface PreRegZipHandlingService {

	/**
	 * This method is used to extract the pre registration packet zip file and reads
	 * the content
	 * 
	 * @param preREgZipFile
	 *            - the pre registration zip file
	 * @return RegistrationDTO - This holds the extracted demographic data and other
	 *         values
	 * @throws RegBaseCheckedException
	 */
	RegistrationDTO extractPreRegZipFile(byte[] preREgZipFile) throws RegBaseCheckedException;

	PreRegistrationDTO encryptAndSavePreRegPacket(String PreRegistrationId, byte[] preRegPacket)
			throws RegBaseCheckedException;

	String storePreRegPacketToDisk(String PreRegistrationId, byte[] encryptedPacket) throws RegBaseCheckedException;

	byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket);

}