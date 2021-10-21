package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Manoj SP
 *
 */
@Getter 
@Setter 
@ToString 
@Entity
@IdClass(HotListCachePK.class)
@NoArgsConstructor
@Table(schema = "ida", name = "hotlist_cache")
public class HotlistCache {

	@Id
	@Column(name = "id_hash")
	public String idHash;
	
	@Id
	@Column(name = "id_type")
	public String idType;
	
	@Column(name = "status")
	public String status;
	
	@Column(name = "start_timestamp")
	public LocalDateTime startDTimes;
	
	@Column(name = "expiry_timestamp")
	public LocalDateTime expiryDTimes;

}
