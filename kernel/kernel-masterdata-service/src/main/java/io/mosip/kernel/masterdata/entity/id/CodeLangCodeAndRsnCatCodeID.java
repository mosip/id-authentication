package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CodeLangCodeAndRsnCatCodeID implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3909028803659392056L;

	@Column(name = "rsncat_code", nullable = false, length = 36)
	private String rsnCatCode;

	@Column(name = "code", nullable = false, length = 36)
	private String code;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

}
