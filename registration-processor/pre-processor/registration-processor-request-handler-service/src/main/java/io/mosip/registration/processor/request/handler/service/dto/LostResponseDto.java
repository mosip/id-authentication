/**
 * 
 */
package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author M1022006
 *
 */
@Data
public class LostResponseDto implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 4000123785627519447L;

	/** The id Value. */
	private String idValue;
}
