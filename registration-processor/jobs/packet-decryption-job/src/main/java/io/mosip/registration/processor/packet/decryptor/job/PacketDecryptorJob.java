package io.mosip.registration.processor.packet.decryptor.job;

/**
 * Interface for PacketDecryptorJob
 * @author M1030448
 *
 * @param <T>
 */
public interface PacketDecryptorJob<T> {
	/**
	 * method to get PacketDecryptorJob bean
	 * @return job PacketDecryptorJob
	 */
	T packetDecryptorJob(); 
}
