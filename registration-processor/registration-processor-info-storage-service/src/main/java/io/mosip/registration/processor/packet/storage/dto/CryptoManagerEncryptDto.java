package io.mosip.registration.processor.packet.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoManagerEncryptDto {

	private String aad;
	private String applicationId;
	private String data;
	private String referenceId;
	private String salt;
	private String timeStamp;

}
