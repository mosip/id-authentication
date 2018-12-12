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
@Entity
@Table(name = "template_file_format", schema = "master")
@EqualsAndHashCode(callSuper = false)
public class TemplateFileFormat extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1051422672381211978L;

	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "descr")
	private String description;

	@Column(name = "lang_code")
	private String langCode;
	
}
