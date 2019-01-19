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

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "module_detail", schema = "master")
public class ModuleDetail  extends BaseEntity implements Serializable {
	

	  
	/**
	 * 
	 */
	private static final long serialVersionUID = 3084280743182524889L;

		@Id
	  	@Column(name = "id", nullable = false, length = 36)
		private String id;
		
		@Column(name = "lang_code", nullable = false, length = 3)
		private String langCode;

		@Column(name = "name", nullable = false, length = 64)
		private String name;

		@Column(name = "descr", length = 128)
		private String description;

}
