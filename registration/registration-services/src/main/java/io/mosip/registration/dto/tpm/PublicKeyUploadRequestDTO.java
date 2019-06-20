package io.mosip.registration.dto.tpm;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The request class for uploading the TPM Public Key to the server
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class PublicKeyUploadRequestDTO {

	/** The machine name. */
	private String machineName;

	/** The encoded TPM public key. */
	private String publicKey;

}
