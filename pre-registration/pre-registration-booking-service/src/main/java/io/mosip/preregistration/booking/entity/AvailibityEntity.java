package io.mosip.preregistration.booking.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Component
@Entity
@Table(name = "reg_available_slot", schema = "prereg")
@Getter
@Setter
@NoArgsConstructor
@ToString
@IdClass(AvailabilityPK.class)
public class AvailibityEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id	
	@AttributeOverrides({ @AttributeOverride(name = "regcntrId", column = @Column(name = "regcntr_id")),
		@AttributeOverride(name = "regDate", column = @Column(name = "availability_date")),
		@AttributeOverride(name = "fromTime", column = @Column(name = "slot_from_time")),
		@AttributeOverride(name = "toTime", column = @Column(name = "slot_to_time"))})
	
	private String regcntrId;
	private String regDate;
	private LocalTime fromTime;
	private LocalTime toTime;
	
	@Column(name = "isActive")
	private Boolean isActive;
	
	@Column(name = "availabilityNo")
	private int availabilityNo;
	
	@Column(name = "available_kiosks")
	private int availableKiosks;

	@Column(name = "cr_by")
	private String crBy;

	@Column(name = "cr_dtimes")
	private Timestamp crDate;

	@Column(name = "upd_by")
	private String upBy;

	@Column(name = "upd_dtimes")
	private Timestamp updDate;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private Timestamp delTime;

}
