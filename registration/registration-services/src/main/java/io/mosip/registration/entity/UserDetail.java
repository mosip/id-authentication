package io.mosip.registration.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * The Entity Class for User Detail details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "user_detail")
@Getter
@Setter
public class UserDetail extends RegistrationCommonFields implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "reg_id")
	private String regid;

	@Column(name = "salt")
	private String salt;

	@Column(name = "name")
	private String name;

	@Column(name = "email")
	private String email;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "last_login_dtimes")
	private Timestamp lastLoginDtimes;

	@Column(name = "last_login_method")
	private String lastLoginMethod;

	@Column(name = "unsuccessful_login_count")
	private Integer unsuccessfulLoginCount;

	@Column(name = "userlock_till_dtimes")
	private Timestamp userlockTillDtimes;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "userDetail")
	private Set<UserRole> userRole;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "userDetail")
	private Set<UserMachineMapping> userMachineMapping;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "userDetail")
	private Set<UserBiometric> userBiometric;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "userDetail")
	private UserPassword userPassword;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "userDetail")
	private RegCenterUser regCenterUser;

}
