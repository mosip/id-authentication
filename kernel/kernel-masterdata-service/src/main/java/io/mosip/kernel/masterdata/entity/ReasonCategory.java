package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

@Entity
@Table(name = "reason_category")
public class ReasonCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1440279821197074364L;
	
	@Column(name="code",nullable=false,length=36)
	private String code;
	
	@Column(name="name",nullable=false,length=64)
	private String name;
	
	/*@Column(name="descr",length=128)
	private String */

}
