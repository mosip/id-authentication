package org.mosip.registration.processor.util.store;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.file.FileUtil;
import org.mosip.registration.processor.exception.RegBaseCheckedException;
import org.mosip.registration.processor.exception.RegBaseUncheckedException;
import org.mosip.registration.processor.consts.RegProcessorExceptionCode;

import static org.mosip.registration.processor.consts.RegConstants.ZIP_FILE_EXTENSION;
import static org.mosip.registration.processor.consts.RegProcessorExceptionEnum.REG_IO_EXCEPTION;

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
