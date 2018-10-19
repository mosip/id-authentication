package io.mosip.authentication.service.impl.indauth.service.demo;

import java.io.Serializable;

import lombok.Data;

@Data
public class LocationEntityPK implements Serializable {

	private static final long serialVersionUID = -6083330306515116063L;

	private String code;

	private boolean isActive;

}
