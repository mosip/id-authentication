package io.mosip.idrepository.core.spi;

import java.util.List;

/**
 * The Interface MosipFingerprintProvider.
 *
 * @author Manoj SP
 * 
 */
public interface MosipFingerprintProvider<A, R> {

	/**
	 * Convert FIR to FMR.
	 *
	 * @param listOfBIR the list of BIR
	 * @return the list
	 */
	List<R> convertFIRtoFMR(List<A> listOfBIR);
}
