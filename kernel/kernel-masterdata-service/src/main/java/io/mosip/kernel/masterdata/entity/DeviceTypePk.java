/**
 * 
 */
package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author M1047717
 *
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypePk implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2016138253768704918L;

	@Column(name = "code", nullable = false, length = 64)
	private String code;
	
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
	
}
