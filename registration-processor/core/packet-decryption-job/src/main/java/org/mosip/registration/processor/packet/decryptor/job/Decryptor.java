package org.mosip.registration.processor.packet.decryptor.job;

import java.io.InputStream;
import java.util.Random;

import org.springframework.stereotype.Component;
@Component
public class Decryptor {
	private static final Random random= new Random();
	
	

	/**random method for decryption
	 * @param encryptedPacket
	 * @param registrationId
	 * @return
	 */
	public InputStream decrypt(InputStream encryptedPacket, String registrationId) {

		return encryptedPacket;
	}
	}