package io.mosip.kernel.syncdata.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * {@link RegisteredDeviceDto}
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredDeviceDto extends BaseDto {

	private String code;

	private String dTypeCode;

	private String dsTypeCode;

	private String statusCode;

	private String deviceId;

	private String deviceSubId;

	private String serialNumber;

	private String providerId;

	private String providerName;

	private String purpose;

	private String firmware;

	private String make;

	private String model;

	private LocalDateTime expiryDate;

	private String certificationLevel;

	private String foundationalTPId;

	private String foundationalTrustSignature;

	private String foundationalTrustCertificate;

	private byte[] dProviderSignature;
}
