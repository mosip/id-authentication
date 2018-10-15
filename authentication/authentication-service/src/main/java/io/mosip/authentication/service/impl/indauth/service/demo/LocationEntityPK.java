package io.mosip.authentication.service.impl.indauth.service.demo;

import java.io.Serializable;

import lombok.Data;

@Data
public class LocationEntityPK implements Serializable {

	private String code;

	private boolean isActive;

}
