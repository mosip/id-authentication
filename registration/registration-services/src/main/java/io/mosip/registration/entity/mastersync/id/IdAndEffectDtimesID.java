package io.mosip.registration.entity.mastersync.id;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Embeddable

public class IdAndEffectDtimesID implements Serializable {

	private static final long serialVersionUID = 7001663925687776491L;

	@Column(name = "id")
	private String id;

	@Column(name = "eff_dtimes")
	private LocalDateTime effectDtimes;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the effectDtimes
	 */
	public LocalDateTime getEffectDtimes() {
		return effectDtimes;
	}

	/**
	 * @param effectDtimes the effectDtimes to set
	 */
	public void setEffectDtimes(LocalDateTime effectDtimes) {
		this.effectDtimes = effectDtimes;
	}
	
	

}
