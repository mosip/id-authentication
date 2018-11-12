package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="reason_list")
public class ReasonList implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -572990183711593868L;
	@Id
	@Column(name="code",nullable=false,length=36)
	private String code;
	
	@Column(name="name",nullable=false,length=64)
	private String name;
	
	@Column(name="descr",length=256)
	private String description;
	
	@Id
	@Column(name="rsncat_code",nullable=false,length=36)
	private String reasonCategoryCode;
	
	@Id
	@Column(name="lang_code",nullable=false,length=3)
	private String langCode;
	
	@Column(name="is_active")
	private boolean isActive;
	
	@Column(name="is_deleted")
	private boolean isDeleted;
	

}
