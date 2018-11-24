package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DataSync entity
 * 
 * @author M1046129 - Jagadishwari
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class ReverseDataSyncDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3615477037570600620L;

	private String id;
	private String ver;
	private Timestamp reqTime;
	private ReverseDataSyncRequestDTO request;
}
