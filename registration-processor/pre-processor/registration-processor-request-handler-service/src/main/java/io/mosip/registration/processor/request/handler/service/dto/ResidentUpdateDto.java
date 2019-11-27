package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	private String transactionID;
	private String idValue;
	private ResidentIndividialIDType idType;
	@JsonIgnore
	private RegistrationType requestType = RegistrationType.RES_UPDATE;
	private String otp;
	private String centerId;
	private String machineId;
	private String identityJson;
	private String proofOfAddress;
	private String proofOfIdentity;
	private String proofOfRelationship;
	private String proofOfDateOfBirth;

}
