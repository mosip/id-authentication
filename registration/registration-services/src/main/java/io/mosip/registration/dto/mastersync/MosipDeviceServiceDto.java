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
public class MosipDeviceServiceDto extends MasterSyncBaseDto {

	private String id;
	private Blob swBinaryHash;
	private String swVersion;
	private String dProviderId;
	private String dTypeCode;
	private String dsTypeCode;
	private String make;
	private String model;
	private Timestamp swCrDtimes;
	private Timestamp swExpiryDtimes;
	private Boolean isActive;
	private String crBy;
	private Timestamp crDtime;
	private String updBy;
	private Timestamp updDtimes;
	private Timestamp delDtimes;
}

