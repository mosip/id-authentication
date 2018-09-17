/**
 * 
 */
package org.mosip.kernel.idvalidator.uinvalidator;

import org.mosip.kernel.core.spi.idvalidator.MosipIdValidator;
import org.mosip.kernel.core.utils.MosipIdChecksum;
import org.mosip.kernel.core.utils.MosipIdFilter;

public class UinValidator implements MosipIdValidator<String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.spi.idvalidator.MosipIdValidator#validateId(java.lang.
	 * Object)
	 */
	public boolean validateId(String id) {

		if (!MosipIdChecksum.validateChecksum(id)) {
			return false;
		} 
		if (!MosipIdFilter.isValidId(id)) {
			return false;
		}
		return true;
	}

}