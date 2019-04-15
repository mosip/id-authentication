package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.registration.entity.id.IdAndLanguageCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The Entity Class for ProcessList.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@IdClass(IdAndLanguageCodeID.class)
@Entity
@Table(name = "process_list", schema = "reg")
public class ProcessList extends RegistrationCommonFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5669845046061622990L;
	
	/** The id. */
	@Id
	private String id;

	/** The name. */
	@Column(name="name")
	private String name;
	
	/** The descr. */
	@Column(name="descr")
	private String descr;
	
	/** The lang code. */
	@Id
	private String langCode;

}
