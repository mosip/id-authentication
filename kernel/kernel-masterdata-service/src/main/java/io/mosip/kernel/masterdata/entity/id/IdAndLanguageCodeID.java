package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdAndLanguageCodeID implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	@Column(name = "id", nullable = false)
	private String id;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

}
