package io.mosip.kernel.syncdata.entity.id;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdAndEffectDtimesID implements Serializable{
	
	private static final long serialVersionUID = 7001663925687776491L;

	@Column(name = "id", nullable = false)
	private String id;

	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectDtimes;

}
