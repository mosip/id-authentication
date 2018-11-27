package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ReasonCategoryId implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3035455749747854356L;



	@Column(name = "code", nullable = false)
	private String code;
	
	
	
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
	
	
}