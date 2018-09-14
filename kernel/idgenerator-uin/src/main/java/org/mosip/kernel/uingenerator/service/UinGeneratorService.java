/**
 * 
 */
package org.mosip.kernel.uingenerator.service;

import org.mosip.kernel.uingenerator.dto.UinResponseDto;
import org.mosip.kernel.uingenerator.exception.UinNotFoundException;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface UinGeneratorService {

	UinResponseDto getId() throws UinNotFoundException;

}