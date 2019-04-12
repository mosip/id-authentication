package io.mosip.registration.processor.packet.receiver.service;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.packet.receiver.exception.PacketReceiverAppException;

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
public interface PacketReceiverService<T, U> {

	/**
	 * Stores registration packets to virus scanner zone.
	 *
	 * @param file
	 *            the file
	 * @return the u
	 */
	public U validatePacket(T file, String stageName) throws PacketReceiverAppException;

}
