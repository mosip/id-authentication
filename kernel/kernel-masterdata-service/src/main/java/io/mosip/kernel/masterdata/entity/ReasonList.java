package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "reason_list", schema = "master")
public class ReasonList extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -572990183711593868L;
	@Id
	@Column(name = "code", nullable = false, length = 36)
	private String code;

	@Column(name = "name", nullable = false, length = 64)
	private String name;

	@Column(name = "descr", length = 256)
	private String description;
    
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "rsncat_code")
	private ReasonCategory reasonCategoryCode;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

}
