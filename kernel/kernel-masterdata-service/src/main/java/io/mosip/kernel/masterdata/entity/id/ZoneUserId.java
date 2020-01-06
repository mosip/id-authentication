package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneUserId implements Serializable {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -4334937137252000426L;

	@Column(name = "zone_code", unique = true, nullable = false, length = 36)
	private String zoneCode;

	@Column(name = "usr_id", unique = true, nullable = false, length = 256)
	private String userId;
}
