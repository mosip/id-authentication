package io.mosip.resident.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegProcReprintResponseDto implements Serializable{
	private static final long serialVersionUID = 8210967648054130280L;
	private String registrationId;
	private String status;
	private String message;
}
