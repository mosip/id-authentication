package io.mosip.authentication.core.partner.dto;

import lombok.Data;

/**
 * License
 * 
 * @author Loganathan Sekar
 *
 */
@Data
public class License {
	private String licenseKey;
	private String mispId;
	private String expiryDt;
	private String status;
}
