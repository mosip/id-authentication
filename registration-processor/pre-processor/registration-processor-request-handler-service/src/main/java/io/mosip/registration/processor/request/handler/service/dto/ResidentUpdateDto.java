package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import org.json.simple.JSONObject;

import io.mosip.registration.processor.status.code.RegistrationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Girish Yarru
 * @since 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentUpdateDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8491761257330824671L;

	// private String transactionID;
	private String individualId;
	private ResidentIndividialIDType individualIdType;
	private RegistrationType requestType;
	// private String otp;
	private String centerId;
	private String machineId;
	private JSONObject demographics;

	public static void main(String[] args) {

	}

}
