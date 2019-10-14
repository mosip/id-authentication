package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProviderDto extends BaseDto {
	/** The id. */
	
	private String id;

	/** The vendor name. */
	
	private String vendorName;

	/** The address. */
	
	private String address;

	/** The email. */
	
	private String email;

	/** The contact number. */
	
	private String contactNumber;

	/** The certificate alias. */
	private String certificateAlias;
}
