package io.mosip.registration.processor.packet.uploader.service;

import org.springframework.stereotype.Service;

/**
 * This service is used to store the registration packets to virus scanner zone,
 * check duplicate packets etc.
 *
 * @param <T>
 *            Type of file
 * @param <U>
 *            Return type of operations
 */
@Service
public interface PacketUploaderService<U> {

	/**
	 * Stores registration packets to virus scanner zone.
	 *
	 * @param file
	 *            the file
	 * @return the u
	 */
	public U validateAndUploadPacket(String regId);

}
