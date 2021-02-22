package io.mosip.authentication.common.service.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotListCachePK implements Serializable {
	
	private static final long serialVersionUID = -5486043175814831027L;

	public String idHash;
	
	public String idType;

}
