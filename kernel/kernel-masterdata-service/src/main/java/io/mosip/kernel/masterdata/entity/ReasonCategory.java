package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reason_category", schema = "master")
public class ReasonCategory extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1440279821197074364L;

	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;

	@Column(name = "lang_code")
	private String languageCode;

	@OneToMany(mappedBy = "reasonCategoryCode",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<ReasonList> reasons;

	public ReasonCategory(String code, String name, String descr, String langCode, List<ReasonList> reasons,
			Boolean isActive, Boolean isDeleted, String createdBy, String updatedBy, LocalDateTime createdTime,
			LocalDateTime updatedTime, LocalDateTime deletedTime) {

		this.code = code;
		this.name = name;
		this.description = descr;
		this.languageCode = langCode;
		this.reasons = reasons;
		setIsActive(isActive);
		setIsDeleted(isDeleted);
		setCreatedBy(createdBy);
		setUpdatedBy(updatedBy);
		setCreatedtimes(createdTime);
		setUpdatedtimes(updatedTime);
		setDeletedtimes(deletedTime);

	}

}
