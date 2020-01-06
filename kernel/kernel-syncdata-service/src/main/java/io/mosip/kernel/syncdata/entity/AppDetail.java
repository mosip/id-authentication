package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;

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

/**
 * Class AppDetail.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */

@Data

/**
 * Instantiates a new app detail.
 */
@NoArgsConstructor

/**
 * Instantiates a new app detail.
 *
 * @param id       the id
 * @param name     the name
 * @param descr    the descr
 * @param langCode the lang code
 */
@AllArgsConstructor

/*
 * (non-Javadoc)
 * 
 * @see io.mosip.kernel.syncdata.entity.BaseEntity#hashCode()
 */
@EqualsAndHashCode(callSuper = false)
@IdClass(IdAndLanguageCodeID.class)
@Entity
@Table(name = "app_detail", schema = "master")
public class AppDetail extends BaseEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8290595452364059572L;

	/** The id. */
	@Id
	private String id;

	/** The name. */
	@Column(name = "name", length = 64, nullable = false)
	private String name;

	/** The descr. */
	@Column(name = "descr", length = 256)
	private String descr;

	/** The lang code. */
	@Id
	private String langCode;

}
