package io.mosip.authentication.service.impl.indauth.service.demo;

import java.io.Serializable;

import lombok.Data;

/**
 * Instantiates a new demo entitiy PK.
 */
@Data
public class DemoEntitiyPK implements Serializable {

	private static final long serialVersionUID = 2569780071564639646L;

	private String uinRefId;

	private String langCode;

}
