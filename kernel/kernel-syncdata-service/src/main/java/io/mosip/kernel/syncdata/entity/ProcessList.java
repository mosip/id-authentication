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
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@IdClass(IdAndLanguageCodeID.class)
@Entity
@Table(name = "process_list", schema = "master")
public class ProcessList extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5669845046061622990L;

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
