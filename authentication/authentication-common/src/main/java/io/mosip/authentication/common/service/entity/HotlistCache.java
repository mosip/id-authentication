package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

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
	public String idHash;
	
	@Id
	public String idType;
	
	public String status;
	
	public LocalDateTime startDTimes;
	
	public LocalDateTime expiryDTimes;

}
