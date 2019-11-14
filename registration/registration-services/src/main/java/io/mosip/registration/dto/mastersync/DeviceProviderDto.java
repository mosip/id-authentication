package io.mosip.registration.dto.mastersync;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProviderDto  extends MasterSyncBaseDto {

	private String id;
	private String vendorName;
	private String address;
	private String email;
	private String contactNumber;
	private String certificateAlias;
	private Boolean isActive;
	private String crBy;
	private Timestamp crDtime;
	private String updatedBy;
	private Timestamp updatedDateTimes;
	private Timestamp delTime;

}
