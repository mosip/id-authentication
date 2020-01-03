/**
 * 
 */
package io.mosip.resident.dto;

import lombok.Data;

/**
 * @author M1022006
 *
 */
@Data
public class AuthTypeStatus {

	private String authSubType;

	private String authType;

	private boolean locked;
}
