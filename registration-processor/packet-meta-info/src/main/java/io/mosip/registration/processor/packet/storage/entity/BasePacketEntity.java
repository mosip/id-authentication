package io.mosip.registration.processor.packet.storage.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
/**
 * 
 * @author Girish Yarru
 *
 * @param <C>
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BasePacketEntity<C> {

	@EmbeddedId
	protected C id;

	public C getId() {
		return id;
	}

	public void setId(C id) {
		this.id = id;
	}

}
