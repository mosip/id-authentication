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
public class FoundationalTrustProviderDto extends MasterSyncBaseDto {

	private String id;
	private String name;
	private String address;
	private String email;
	private String contactNumber;
	private String certificateAlias;
	private Boolean isActive;
	private String crBy;
	private Timestamp crDtime;
	private String updBy;
	private Timestamp updDtimes;
	private Timestamp delDtimes;
}
