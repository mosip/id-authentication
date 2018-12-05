package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doc_type", schema = "master")
@IdClass(CodeAndLanguageCodeId.class)
public class DocumentType extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", nullable = false)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3)) })
	private String code;

	private String langCode;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "descr")
	private String description;

}
