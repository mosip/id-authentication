package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserHistoryID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reg_center_user_h", schema = "master")
@IdClass(RegistrationCenterUserHistoryID.class)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCenterUserHistory extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5133215585946271578L;

	@Id
	private String regCntrId;

	@Id
	private String userId;

	@Id
	private LocalDateTime effectDateTimes;

	@Column(name = "lang_code")
	private String langCode;

}
