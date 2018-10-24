package io.mosip.kernel.ridgenerator.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The entity for rid generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "rid", schema = "ids")
public class RidEntity {

	/**
	 * the dongleId.
	 */
	@Id
	private String dongle_Id;

	/**
	 * the sequenceId.
	 * 
	 */
	@Column(name = "sequence_id")
	private int sequenceId;

	/**
	 * Getter for dongleId.
	 * 
	 * @return
	 */
	public String getDongleId() {
		return dongle_Id;
	}

	/**
	 * Setter for dongleId.
	 * 
	 * @param dongleId
	 */
	public void setDongleId(String dongleId) {
		this.dongle_Id = dongleId;
	}

	/**
	 * Getter for sequencyId.
	 * 
	 * @return
	 */
	public int getSequenceId() {
		return sequenceId;
	}

	/**
	 * Setter for sequenceId.
	 * 
	 * @param sequenceId
	 */
	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

}