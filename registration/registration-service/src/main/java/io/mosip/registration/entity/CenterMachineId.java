package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;


/**
 * EmbeddedId for {@link CenterMachine}
 * @author Dinesh Ashokan
 *
 */
public class CenterMachineId implements Serializable{
	
	
	private static final long serialVersionUID = 241072783610318336L;

	@Column(name = "machine_id", length = 64, nullable = false)
	private String id;
	@Column(name = "regcntr_id", length = 32, nullable = false)
	private String centreId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCentreId() {
		return centreId;
	}
	public void setCentreId(String centreId) {
		this.centreId = centreId;
	}
		
}
