package io.mosip.pregistration.datasync.entity;

import java.io.Serializable;
import java.sql.Timestamp;

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
public class Ipprlst_PK implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2690916920321347697L;
	protected String prereg_id;
	protected Timestamp received_dtimes;

}
