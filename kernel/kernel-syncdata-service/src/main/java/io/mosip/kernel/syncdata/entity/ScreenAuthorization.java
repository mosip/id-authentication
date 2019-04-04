package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.syncdata.entity.id.ScreenAuthorizationID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Class ScreenAuthorization.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */

/*
 * (non-Javadoc)
 * 
 * @see io.mosip.kernel.syncdata.entity.BaseEntity#toString()
 */
@Data

/*
 * (non-Javadoc)
 * 
 * @see io.mosip.kernel.syncdata.entity.BaseEntity#hashCode()
 */
@EqualsAndHashCode(callSuper = false)

/**
 * Instantiates a new screen authorization.
 */
@NoArgsConstructor

/**
 * Instantiates a new screen authorization.
 *
 * @param screenId    the screen id
 * @param roleCode    the role code
 * @param langCode    the lang code
 * @param isPermitted the is permitted
 * 
 */
@AllArgsConstructor
@Entity
@Table(name = "screen_authorization", schema = "master")
@IdClass(ScreenAuthorizationID.class)
public class ScreenAuthorization extends BaseEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8577795844996783253L;

	/** The screen id. */
	@Id
	private String screenId;

	/** The role code. */
	@Id
	private String roleCode;

	/** The lang code. */
	@Column(name = "lang_code", length = 3, nullable = false)
	private String langCode;

	/** The is permitted. */
	@Column(name = "is_permitted", nullable = false)
	private Boolean isPermitted;

}
