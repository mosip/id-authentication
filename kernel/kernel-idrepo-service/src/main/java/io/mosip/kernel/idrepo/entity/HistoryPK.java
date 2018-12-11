package io.mosip.kernel.idrepo.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class HistoryPK.
 *
 * @author Manoj SP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryPK implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1124172782509039861L;
	
	/** The uin ref id. */
	private String uinRefId;
	
	/** The effective date time. */
	private Date effectiveDateTime;

}
