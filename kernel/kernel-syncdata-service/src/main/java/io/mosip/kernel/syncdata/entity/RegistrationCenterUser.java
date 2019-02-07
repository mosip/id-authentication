package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;

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

/**
 * Entity class to map Registeration center id and user id
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "reg_center_user", schema = "master")
public class RegistrationCenterUser extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 3941306023356031908L;
	
	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "regCenterId", column = @Column(name = "regcntr_id")),
			@AttributeOverride(name = "userId", column = @Column(name = "usr_Id")) })
	private RegistrationCenterUserID registrationCenterUserID;

}
