/**
 * 
 */
package io.mosip.resident.dto;

import java.io.Serializable;

import org.json.simple.JSONArray;

import lombok.Data;

/**
 * @author M1022006
 *
 */
@Data
public class AuthLockRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String transactionID;
	private String individualId;
	private String individualIdType;
	private String otp;
	private JSONArray authType;

}
