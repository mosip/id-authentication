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
public class ReasonListId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5622889820282234362L;

	@Column(name = "rsncat_code", nullable = false, length = 36)
	private String rsnCatCode;

	@Column(name = "code", nullable = false, length = 36)
	private String code;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

}
