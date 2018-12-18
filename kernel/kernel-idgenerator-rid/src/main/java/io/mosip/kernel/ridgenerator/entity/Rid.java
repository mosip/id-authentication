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
public class Rid {

	/**
	 * the dongleId.
	 */
	@Id
	@Column(name = "dongle_Id")
	private String dongleId;

	/**
	 * the sequenceId.
	 * 
	 */
	@Column(name = "sequence_id")
	private int sequenceId;

	/**
	 * Getter for dongleId.
	 * 
	 * @return dongleId
	 */
	public String getDongleId() {
		return dongleId;
	}

	/**
	 * Setter for dongleId.
	 * 
	 * @param dongleId
	 *            dongleId
	 */
	public void setDongleId(String dongleId) {
		this.dongleId = dongleId;
	}

	/**
	 * Getter for sequencyId.
	 * 
	 * @return sequenceId
	 */
	public int getSequenceId() {
		return sequenceId;
	}

	/**
	 * Setter for sequenceId.
	 * 
	 * @param sequenceId
	 *            sequenceId
	 */
	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

}