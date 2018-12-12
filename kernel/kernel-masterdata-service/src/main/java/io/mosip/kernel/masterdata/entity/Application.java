package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "appl_form_type", schema = "master")
public class Application extends BaseEntity implements Serializable {
	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 893244317356416503L;

	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;

	@Column(name = "lang_code")
	private String langCode;

}
