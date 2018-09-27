package org.mosip.registration.util.store;

import static org.mosip.registration.consts.RegConstants.ZIP_FILE_EXTENSION;
import static org.mosip.registration.consts.RegProcessorExceptionEnum.REG_IO_EXCEPTION;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.registration.consts.RegProcessorExceptionCode;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.kernel.core.utils.FileUtil;

/**
 * Class to Store the Packets in local disk
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class StorageManager {

	/**
	 * Writes the encrypted packet to the local storage
	 * 
	 * @param zipFileName
	 *            the name of the file
	 * @param packet
	 *            the encrypted packet data to be stored in local storage
	 * @throws RegBaseCheckedException 
	 */
	public static void storeToDisk(String zipFileName, byte[] packet) throws RegBaseCheckedException {
		try {
			FileUtil.copyToFile(new ByteArrayInputStream(packet), new File(zipFileName.concat(ZIP_FILE_EXTENSION)));
		} catch (MosipIOException ioException) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.ENCRYPTED_PACKET_STORAGE, uncheckedException.getMessage());
		}
	}
}
