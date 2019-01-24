package io.mosip.kernel.syncdata.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "reg_center_user", schema = "master")

public class RegistrationCenterUser extends BaseEntity {

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "regCenterId", column = @Column(name = "regcntr_id")),
			@AttributeOverride(name = "userId", column = @Column(name = "usr_Id")) })
	private RegistrationCenterUserID registrationCenterUserID;

}
