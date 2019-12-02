package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
	@NotNull(message = "idValue should not be null ")
	@NotBlank(message = "idValue should not be empty")
	private String idValue;
	@NotNull(message = "idType should not be null ")
	private ResidentIndividialIDType idType;
	@JsonIgnore
	private RegistrationType requestType = RegistrationType.RES_UPDATE;
	@NotNull(message = "centerId should not be null ")
	@NotBlank(message = "centerId should not be empty")
	private String centerId;
	@NotNull(message = "machineId should not be null ")
	@NotBlank(message = "machineId should not be empty")
	private String machineId;
	@NotNull(message = "identityJson(encoded base64) should not be null ")
	@NotBlank(message = "identityJson(encoded base64) should not be empty")
	private String identityJson;
	private String proofOfAddress;
	private String proofOfIdentity;
	private String proofOfRelationship;
	private String proofOfDateOfBirth;

}
