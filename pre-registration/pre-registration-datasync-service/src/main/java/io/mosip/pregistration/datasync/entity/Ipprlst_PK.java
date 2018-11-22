package io.mosip.pregistration.datasync.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class Ipprlst_PK implements Serializable{
	protected String prereg_id;
	protected Timestamp received_dtimes;

}
