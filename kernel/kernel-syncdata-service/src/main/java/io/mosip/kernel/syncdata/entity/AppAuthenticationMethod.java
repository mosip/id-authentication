/**
 * 
 */
package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.syncdata.entity.id.AppAuthenticationMethodID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Srinivasan
 * @since 1.0.0
 *
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AppAuthenticationMethodID.class)
@Entity
@Table(name = "app_authentication_method", schema = "master")
public class AppAuthenticationMethod extends BaseEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1289945821993829289L;

	/** The app id. */
	@Id
	private String appId;

	/** The process id. */
	@Id
	private String processId;

	/** The role code. */
	@Id
	private String roleCode;

	/** The auth method code. */
	@Id
	private String authMethodCode;

	/** The method sequence. */
	@Column(name = "method_seq")
	private Integer methodSequence;

	/** The lang code. */
	@Column(name = "lang_code", length = 3, nullable = false)
	private String langCode;

}
