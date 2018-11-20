package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@IdClass(ReasonCategoryId.class)
public class ReasonCategory extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1440279821197074364L;

	
	@Id
	@Column(name = "code", nullable = false)
	private String code;
	
	
	@Id
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

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
		

		this.name = name;
		this.description = description;
	    setCode(code);
		setLangCode(langCode);
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
