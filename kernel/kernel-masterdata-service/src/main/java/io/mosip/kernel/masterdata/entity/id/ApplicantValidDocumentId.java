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
public class ApplicantValidDocumentId implements Serializable {

	private static final long serialVersionUID = -9083898959072007739L;

	@Column(name = "apptyp_code ", nullable = false, length = 36)
	private String appTypeCode;

	@Column(name = "doccat_code ", nullable = false, length = 36)
	private String docCategoryCode;

	@Column(name = "doctyp_code  ", nullable = false, length = 36)
	private String docTypeCode;

}
