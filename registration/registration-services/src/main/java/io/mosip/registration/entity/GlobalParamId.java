package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


/**
 * Composite key for GlobalParam entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Getter
@Setter
public class GlobalParamId implements Serializable{

	private static final long serialVersionUID = 4798525506099635089L;
	
	@Column(name = "code")
	private String code;
	@Column(name = "lang_code")
	private String langCode;	

}
