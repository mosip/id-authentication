package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * UserBiometric entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "user_biometric")
@Getter
@Setter
public class UserBiometric extends RegistrationCommonFields {

	@EmbeddedId
	private UserBiometricId userBiometricId;
	
	@Lob
	@Column(name = "bio_raw_image")
	private byte[] bioRawImage;
	@Column(name = "bio_minutia")
	private String bioMinutia;
	@Lob
	@Column(name = "bio_iso_image")
	private byte[] bioIsoImage;
	@Column(name = "quality_score")
	private Integer qualityScore;
	@Column(name = "no_of_retry")
	private Integer numberOfRetry;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	@ManyToOne
	@JoinColumn(name = "usr_id", insertable = false, updatable = false)
	private UserDetail userDetail;

}
