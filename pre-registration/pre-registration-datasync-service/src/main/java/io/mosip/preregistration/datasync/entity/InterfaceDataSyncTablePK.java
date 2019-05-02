package io.mosip.preregistration.datasync.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * To define the composite primary key
 * 
 * @author M1046129
 * 
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class InterfaceDataSyncTablePK implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2690916920321347697L;
	
	@Column(name="prereg_id")
	protected String preregId;
	
	@Column(name="received_dtimes")
	protected LocalDateTime receivedDtimes;

}
