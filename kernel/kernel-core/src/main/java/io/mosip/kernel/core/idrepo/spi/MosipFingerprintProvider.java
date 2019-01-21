package io.mosip.kernel.core.idrepo.spi;

import java.util.List;

/**
 * The Interface MosipFingerprintProvider.
 *
 * @author Manoj SP
 * 
 * @param <T> the generic type
 */
public interface MosipFingerprintProvider<T> {

	/**
	 * Convert FIR to FMR.
	 *
	 * @param listOfBIR the list of BIR
	 * @return the list
	 */
	List<T> convertFIRtoFMR(List<T> listOfBIR);
}
