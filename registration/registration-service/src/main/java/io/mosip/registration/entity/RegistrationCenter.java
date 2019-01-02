package io.mosip.registration.entity;

import java.sql.Time;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * RegistrationCenter entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "registration_center")
@Getter
@Setter
public class RegistrationCenter extends RegistrationCommonFields {

	@Id
	@Column(name = "id")
	private String centerId;
	@Column(name = "name")
	private String centerName;
	@Column(name = "cntrtyp_code")
	private String cntrTypCode;
	@Column(name = "addr_line1")
	private String addrLine1;
	@Column(name = "addr_line2")
	private String addrLine2;
	@Column(name = "addr_line3")
	private String addrLine3;
	@Column(name = "latitude")
	private String latitude;
	@Column(name = "longitude")
	private String longitude;
	@Column(name = "location_Code")
	private String locationCode;
	@Column(name = "contact_phone")
	private String contactPhone;
	@Column(name = "number_of_kiosks")
	private Integer numberOfKiosks;
	@Column(name = "working_hours")
	private String workingHours;
	@Column(name = "per_kiosk_process_time")
	private Time perKioskProcessTime;
	@Column(name = "process_start_time")
	private Time processStartTime;
	@Column(name = "process_end_time")
	private Time processEndTime;
	@Column(name = "time_zone")
	private String timeZone;
	@Column(name = "holiday_loc_code")
	private String holidayLocCode;
	@Column(name = "lang_code")
	private String langCode;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;
	
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private RegistrationCenterUser registrationCenterUser;

}
