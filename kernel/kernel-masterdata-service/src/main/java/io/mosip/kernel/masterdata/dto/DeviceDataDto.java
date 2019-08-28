package io.mosip.kernel.masterdata.dto;

import lombok.Data;

@Data
public class DeviceDataDto {
	private String type;
	private String subType;
	private String status;
	private String deviceCode;
	private String deviceId;
	private String deviceProviderName;
	private String deviceProviderId;
	private DeviceInfoDto deviceInfo;
	private String foundationalTrustProviderID;
	private String foundationalTrustSignature;
	private String foundationTrustCertificate;
}
