package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekDayId implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -7044883507018869066L;
	
	@Column(name = "code", nullable = false, length = 3,insertable=false,updatable=false)
	private String code;

	@Column(name="lang_code",nullable=false,length=3)
	private String langCode;

}
