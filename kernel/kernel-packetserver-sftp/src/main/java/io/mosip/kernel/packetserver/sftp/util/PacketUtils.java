package io.mosip.kernel.packetserver.sftp.util;

import java.io.FileReader;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import io.mosip.kernel.packetserver.sftp.constant.PacketServerExceptionConstant;
import io.mosip.kernel.packetserver.sftp.exception.PublicKeyException;

/**
 * Utils class for this server
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Component
public class PacketUtils {

	/**
	 * Constructor for this class
	 */
	private PacketUtils() {
	}

	/**
	 * File start signature
	 */
	private static final String FILE_START = "-----BEGIN RSA PUBLIC KEY-----";
	/**
	 * File end signature
	 */
	private static final String FILE_STOP = "-----END RSA PUBLIC KEY-----";

	/**
	 * Converts a RSA public key to byte array
	 * 
	 * @param fileLocation
	 *            location of file
	 * @return encoded byte array version from public key
	 */
	public byte[] getFileBytes(String fileLocation) {
		String key = null;
		try {
			key = FileCopyUtils.copyToString(new FileReader(new ClassPathResource(fileLocation).getFile()));
		} catch (IOException e) {
			throw new PublicKeyException(PacketServerExceptionConstant.MOSIP_PUBLIC_KEY_EXCEPTION.getErrorCode(),
					PacketServerExceptionConstant.MOSIP_PUBLIC_KEY_EXCEPTION.getErrorMessage());
		}

		return key.replaceAll(FILE_START, "").replaceAll(FILE_STOP, "").getBytes();
	}

}
