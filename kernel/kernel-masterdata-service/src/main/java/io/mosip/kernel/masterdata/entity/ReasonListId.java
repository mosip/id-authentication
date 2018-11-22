package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class ReasonListId implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3035455749747854356L;



	@Column(name = "code", nullable = false)
	private String code;
	
	
	
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "rsncat_code")
	private ReasonCategory reasonCategoryCode;
}
