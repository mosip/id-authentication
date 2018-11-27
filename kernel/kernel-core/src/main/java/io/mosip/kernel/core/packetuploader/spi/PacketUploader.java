package io.mosip.kernel.core.packetuploader.spi;

/**
 * Interface for Packet Uploader SFTP
 * 
 * @author Urvil Joshi
 * @param <S>
 *            the type of packet server configurations
 * @param <C>
 *            the type of SFTP-channel
 * @since 1.0.0
 */
public interface PacketUploader<S, C> {

	/**
	 * This creates and connects SFTP channel based on configutaions
	 * 
	 * @param sftpServer
	 *             packet server configurations provided by user
	 * @return configured SFTP-channel instance
	 */
	C createSFTPChannel(S sftpServer);

	/**
	 * Uploads file to server <i>(this method will not create destination folder
	 * it should be already present)</i>
	 * 
	 * @param sftpChannel
	 *            configured SFTP-channel instance
	 * @param source
	 *            path of packet to be uploaded
	 */
	void upload(C sftpChannel, String source);

	/**
	 * This releases the obtained Connection to server
	 * 
	 * @param sftpChannel
	 *            configured SFTP-channel instance
	 */
	void releaseConnection(C sftpChannel);

}