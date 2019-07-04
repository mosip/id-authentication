package io.mosip.registration.processor.quality.check.entity;
	
import javax.persistence.EmbeddedId;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

/**
 * The Class BaseQcuserEntity.
 *
 * @param <C> the generic type
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseQcuserEntity<C> {
	
	/** The id. */
	@EmbeddedId
	protected C id;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public C getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(C id) {
		this.id = id;
	}
}
