package io.mosip.kernel.syncdata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.syncdata.entity.id.IdAndLanguageCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/* (non-Javadoc)
 * @see io.mosip.kernel.syncdata.entity.BaseEntity#toString()
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.kernel.syncdata.entity.BaseEntity#hashCode()
 */
@EqualsAndHashCode(callSuper=false)

/**
 * Instantiates a new screen detail.
 */
@NoArgsConstructor

/**
 * @author Srinivasan
 * 
 * Instantiates a new screen detail.
 *
 * @param id the id
 * @param appId the app id
 * @param name the name
 * @param descr the descr
 * @param langCode the lang code
 */
@AllArgsConstructor
@IdClass(IdAndLanguageCodeID.class)
@Entity
@Table(name="screen_detail",schema="master")
public class ScreenDetail extends BaseEntity {

	/** The id. */
	@Id
	private String id;
	
	/** The app id. */
	@Column(name="app_id",length=36,nullable=false)
	private String appId;
	
	/** The name. */
	@Column(name="name",length=36,nullable=false)
	private String name;
	
	/** The descr. */
	@Column(name="descr",length=256)
	private String descr;
	
	/** The lang code. */
	@Id
	private String langCode;
}
