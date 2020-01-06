package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.id.RegCenterUserId;
import lombok.Getter;
import lombok.Setter;

/**
 * The Entity Class for Reg Center User details.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "reg_center_user")
@Getter
@Setter
public class RegCenterUser extends RegistrationCommonFields {

	@EmbeddedId
	private RegCenterUserId regCenterUserId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private UserDetail userDetail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "regcntr_id",referencedColumnName="id", insertable = false, updatable = false),
		@JoinColumn(name = "lang_code",referencedColumnName="lang_code", insertable = false, updatable = false) })
	private RegistrationCenter registrationCenter;

	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;
	@Column(name = "lang_code")
	private String langCode;

}
