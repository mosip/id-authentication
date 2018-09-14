package org.mosip.kernel.uingenerator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NamedNativeQueries({
		@NamedNativeQuery(name = "UinBean.countFreeUin", query = "select count(*) from uin where used=?1"),
		@NamedNativeQuery(name = "UinBean.findUnusedUin", query = "select * from uin where used = ?1 ORDER BY RAND() LIMIT 1", resultClass = UinBean.class) })
@Entity(name = "uin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UinBean {

	@Id
	@Column(unique = true)
	private String uin;

	private boolean used;

}
