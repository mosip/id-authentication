package io.mosip.kernel.core.idrepo.spi;

import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;

/**
 * @author Manoj SP
 *
 */
public interface MosipDFSProvider {

	boolean storeFile(String bucketName, String filePathAndName, byte[] fileData) throws IdRepoAppException;
	
	byte[] getFile(String bucketName, String filePathAndName) throws IdRepoAppException;
}
