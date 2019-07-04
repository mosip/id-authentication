package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.id.AppRolePriorityId;
import lombok.Getter;
import lombok.Setter;

/**
 * The Entity Class for AppRolePriority
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "app_role_priority")
@Getter
@Setter
public class AppRolePriority extends RegistrationCommonFields{
	
	@EmbeddedId
	private AppRolePriorityId appRolePriorityId;

	@Column(name = "priority")
	private Integer priority;
	@Column(name = "lang_code")
	private String langCode;
	

}
