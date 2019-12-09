/**
 * 
 */
package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class LostRequestDto.
 *
 * @author M1022006
 */

/**
 * Instantiates a new lost request dto.
 */
@Data
public class LostRequestDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7441998842529547308L;

	/** The id type. */
	private String idType;

	/** The name. */
	private String name;

	/** The postal code. */
	private String postalCode;

	/** The contact type. */
	private String contactType;

	/** The contact value. */
	private String contactValue;

}
