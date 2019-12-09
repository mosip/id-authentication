package io.mosip.resident.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegProcRePrintRequestDto {
	
	private String cardType;
	private String centerId;
	private String id;
	private String idType;
	private String machineId;
	private String reason;
	private String registrationType;

}
