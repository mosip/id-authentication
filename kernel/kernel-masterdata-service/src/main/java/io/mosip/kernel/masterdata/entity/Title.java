package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class mapping title to master data
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@NamedNativeQueries({
		@NamedNativeQuery(name = "Title.getThroughLanguageCode", query = "select code, name, descr , lang_code , is_active , cr_by , cr_dtimes , upd_by , upd_dtimes ,is_deleted , del_dtimes from master.title where lang_code = ?1 and (is_deleted is null or is_deleted = false) and is_active = true ", resultClass = Title.class) })
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "title", schema = "master")
@IdClass(CodeAndLanguageCodeID.class)
public class Title extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1323331283383315822L;

	/*@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, length = 16)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3)) })
	@Column(name = "code", unique = true, nullable = false, length = 16)
	private CodeAndLanguageCodeID id;*/
	@Id
	@Column(name="code")
	private String code;
	@Id
	@Column(name="lang_code",nullable=false)
	private String langCode;

	@Column(name = "name", unique = true, nullable = false, length = 64)
	private String titleName;

	@Column(name = "descr", unique = true, nullable = false, length = 128)
	private String titleDescription;

}
