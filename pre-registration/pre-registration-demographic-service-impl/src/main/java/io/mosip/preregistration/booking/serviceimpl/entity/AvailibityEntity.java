
/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.serviceimpl.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This entity class defines the database table details for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@Component
@Entity
@Table(name = "reg_available_slot", schema = "prereg")
@Getter
@Setter
@NoArgsConstructor
@ToString
@IdClass(AvailabilityPK.class)
@NamedQuery(name = "AvailibityEntity.findDate", query = "SELECT DISTINCT regDate FROM AvailibityEntity where regcntrId=:regcntrId and regDate>=:fromDate and regDate<=:toDate order by regDate ASC")
public class AvailibityEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** Registration center Id. */
	@Id
	@AttributeOverrides({ @AttributeOverride(name = "regcntrId", column = @Column(name = "regcntr_id")),
			@AttributeOverride(name = "regDate", column = @Column(name = "availability_date")),
			@AttributeOverride(name = "fromTime", column = @Column(name = "slot_from_time")) })

	private String regcntrId;
	
	/** Registration date. */
	private LocalDate regDate;
	
	/** From date. */
	private LocalTime fromTime;

	/** Slot to time. */
	@Column(name = "slot_to_time")
	private LocalTime toTime;

	/** Available Kiosks. */
	@Column(name = "available_kiosks")
	private int availableKiosks;

	/** Created by. */
	@Column(name = "cr_by")
	private String crBy;

	/** Created date time. */
	@Column(name = "cr_dtimes")
	private LocalDateTime crDate;

	/** Updated by. */
	@Column(name = "upd_by")
	private String upBy;

	/** Updated date time. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updDate;

	/** Is deleted. */
	@Column(name = "is_deleted")
	private boolean isDeleted;

	/** Delete date time. */
	@Column(name = "del_dtimes")
	private LocalDateTime delTime;

}
