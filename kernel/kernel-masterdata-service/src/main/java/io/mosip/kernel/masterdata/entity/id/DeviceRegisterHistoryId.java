package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegisterHistoryId implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8986103890594107087L;
	
	@Column(name = "code")
	private String deviceCode;
	
	@Column(name="eff_dtimes")
	private LocalDateTime effectivetimes;
}
