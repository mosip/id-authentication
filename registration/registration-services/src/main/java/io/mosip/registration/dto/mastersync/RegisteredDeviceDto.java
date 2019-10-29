package io.mosip.registration.dto.mastersync;

import java.sql.Blob;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredDeviceDto extends MasterSyncBaseDto {

	private String code;
	
	private String deviceTypeCode;
	
	private String deviceSubTypeCode;
	
	private String statusCode;
	
	private String deviceId;
	
	private String deviceSubId;

	private String digitalId;
	
	private String serialNumber;
	
	private String providerId;
	
	private String providerName;
	
	private String purpose;
	
	private String firmware;
	
	private String make;
	
	private String model;
	
	private Timestamp expiryDate;
	
	private String certificationLevel;
	
	private String foundationalTrustSignature;
	
	private Blob foundationalTrustCertificate;
	
	private String deviceProviderSignature;
	
	private Boolean isActive;

	private String crBy;
	
	private Timestamp crDtime;
	
	private String updatedBy;
	
	private Timestamp updatedDateTimes;

	private Timestamp deletedDateTimes;

}
