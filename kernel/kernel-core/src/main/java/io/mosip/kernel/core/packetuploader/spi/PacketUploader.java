package io.mosip.kernel.core.packetuploader.spi;



public interface PacketUploader<S,C> {

	/**
	 * This creates and connects SFTP channel based on configutaions
	 * 
	 * @param sftpServer
	 *            {@link SFTPServer} provided by user
	 * @return configured {@link SFTPChannel} instance
	 * @throws ConnectionException
	 *             to be thrown when there is a exception during connection with
	 *             server
	 */
	C createSFTPChannel(S sftpServer);

	/**
	 * Uploads file to server <i>(this method will not create destination folder it
	 * should be already present)</i>
	 * 
	 * @param sftpChannel
	 *            configured {@link SFTPChannel} instance
	 * @param source
	 *            path of packet to be uploaded
	 */
	void upload(C sftpChannel, String source);

	/** 
	 * This releases the obtained Connection to server
	 * 
	 * @param sftpChannel
	 *            configured {@link SFTPChannel} instance
	 */
	void releaseConnection(C sftpChannel);

}