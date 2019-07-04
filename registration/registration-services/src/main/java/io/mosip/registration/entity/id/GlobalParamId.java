package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Composite key for GlobalParamId.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Embeddable
@Data
@Getter
@Setter
public class GlobalParamId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "code")
	private String code;
	@Column(name = "lang_code")
	private String langCode;

}
