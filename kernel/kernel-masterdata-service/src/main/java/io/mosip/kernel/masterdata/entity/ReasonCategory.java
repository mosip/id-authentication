package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reason_category", schema = "master")

public class ReasonCategory extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1440279821197074364L;

	@EmbeddedId
	private ReasonCategoryId reasonCategoryId;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;

	@OneToMany(mappedBy = "reasonCategory", cascade = CascadeType.ALL)
	private List<ReasonList> reasonList = new ArrayList<>();

	public void addReasonList(ReasonList list) {
		list.setReasonCategory(this);
		this.reasonList.add(list);
	}

	public ReasonCategory(String code, String name, String description, String langCode, List<ReasonList> reasons,
			Boolean isActive, Boolean isDeleted, String createdBy, String updatedBy, LocalDateTime createdTime,
			LocalDateTime updatedTime, LocalDateTime deletedTime) {
		reasonCategoryId = new ReasonCategoryId();

		this.name = name;
		this.description = description;
		reasonCategoryId.setCode(code);
		reasonCategoryId.setLangCode(langCode);
		this.reasonList.addAll(reasons);
		setIsActive(isActive);
		setIsDeleted(isDeleted);
		setCreatedBy(createdBy);
		setUpdatedBy(updatedBy);
		setCreatedtimes(createdTime);
		setUpdatedtimes(updatedTime);
		setDeletedtimes(deletedTime);

	}

}
