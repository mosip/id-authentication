package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.mosip.kernel.synchandler.entity.id.CodeAndLanguageCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Ritesh Sinha
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doc_type", schema = "master")
@IdClass(CodeAndLanguageCodeID.class)
public class DocumentType extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", nullable = false)),
	@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3)) })
	@OneToMany(mappedBy="docTypeCode")
	private String code;
	@OneToMany(mappedBy="langCode")
	private String langCode;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "descr")
	private String description;

}
