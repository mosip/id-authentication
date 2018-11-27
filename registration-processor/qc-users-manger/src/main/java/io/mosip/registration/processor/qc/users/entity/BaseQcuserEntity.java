package io.mosip.registration.processor.qc.users.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseQcuserEntity<C> {
	@EmbeddedId
	protected C id;

	public C getId() {
		return id;
	}

	public void setId(C id) {
		this.id = id;
	}
}
