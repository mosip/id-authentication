package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.CenterMachine;
import lombok.Data;


/**
 * Composite key for {@link CenterMachine}
 *
 * @author Dinesh Ashokan
 * @version 1.0.0
 */
@Embeddable
@Data
public class CenterMachineId implements Serializable{
	
	
	private static final long serialVersionUID = 241072783610318336L;

	@Column(name = "machine_id")
	private String id;
	@Column(name = "regcntr_id")
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
