package io.mosip.resident.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegProcReprintRequestDto implements Serializable {
	private static final long serialVersionUID = -1915152825822879859L;
	private String cardType;
	private String centerId;
	private String id;
	private String idType;
	private String machineId;
	private String reason;
	private String registrationType;

}
