package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_detail", schema = "master")
public class UserDetails extends BaseEntity implements Serializable {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = -8541947587557590379L;

	
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	private String id;

	
	@Column(name = "uin", length = 28)
	private String uin;

	
	@Column(name = "name",nullable = false, length = 64)
	private String name;

	@Column(name = "email", length = 64)
	private String email;

	@Column(name = "mobile", length = 16)
	private String mobile;

	@Column(name = "status_code",nullable = false, length = 36)
	private String statusCode;

	@Column(name = "lang_code",nullable = false, length = 3)
	private String langlangCode;

	
	@Column(name = "last_login_dtimes")
	private LocalTime lastLoginDateTime;

	
	@Column(name = "last_login_method", length = 64)
	private String lastLoginMethod;
	
	
    @OneToMany(mappedBy="usrId",fetch = FetchType.LAZY)
	private List<RegistrationCenterUserMachine> registrationCenterUserMachines;

}
