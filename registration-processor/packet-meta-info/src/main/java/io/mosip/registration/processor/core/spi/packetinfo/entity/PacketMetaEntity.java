package io.mosip.registration.processor.core.spi.packetinfo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
/**
 * 
 * @author Horteppa M1048399
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PacketMetaEntity {
	@Id
	public String id;
}
