package org.mosip.kernel.uingenerator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for uin bean
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@NamedNativeQueries({
		@NamedNativeQuery(name = "UinBean.countFreeUin", query = "select count(*) from ids.uins where used = false"),
		@NamedNativeQuery(name = "UinBean.findUnusedUin", query = "select * from ids.uins where used = false order by random() LIMIT 1", resultClass = UinBean.class) })
@Entity
@Table(name = "uins", schema = "ids")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UinBean {

	/**
	 * Field for uin
	 */
	@Id
	@Column(name = "uin", unique = true, nullable = false, updatable = false, length = 12)
	private String uin;

	/**
	 * Field whether this uin is used
	 */
	@Column(name = "used")
	private boolean used;

}
