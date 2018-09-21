package org.mosip.auth.service.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class AuthRequest {



	@Id
    private String uniqueId;
	
	private String refId;
	
	private String virtualId;
}
