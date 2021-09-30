package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import io.mosip.authentication.core.util.CryptoUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Data
@Table(name = "partner_data", schema = "ida")
@Entity
public class PartnerData {

	@Id
	@NotNull
	@Column(name = "partner_id")
	private String partnerId;
	
	@NotNull
	@Column(name = "partner_name")
	private String partnerName;
	
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Basic(fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Column(name = "certificate_data")
	private byte[] certificateData;
	
	@NotNull
	@Column(name = "partner_status")
	private String partnerStatus;
	
	@NotNull
	@Column(name = "cr_by")
	private String createdBy;

	@NotNull
	@Column(name = "cr_dtimes")
	private LocalDateTime crDTimes;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDTimes;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delDTimes;
	
	public String getCertificateData() {
		return new String(CryptoUtil.decodeBase64Url(new String(this.certificateData)));
	}

	public void setCertificateData(String certificateData) {
		this.certificateData = CryptoUtil.encodeBase64Url(certificateData.getBytes()).getBytes();
	}

}