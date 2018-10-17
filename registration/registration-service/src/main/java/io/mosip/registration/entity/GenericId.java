package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;

import lombok.Data;

@Embeddable
@Data
public class GenericId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2757022596319558123L;
	@Column(name = "code", length = 32, nullable = false)
	private String code;
	@Column(name = "is_active", nullable = false)
	@Type(type = "true_false")
	private boolean isActive;

	

}
