package io.mosip.kernel.keymanagerservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Entity
@Table(name = "key_store", schema = "kernel")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class KeyStore extends BaseEntity {

	@Id
	@Column(name = "id", nullable = false, length = 36)
	private String alias;

	@Column(name = "public_key")
	private byte[] publicKey;

	@Column(name = "private_key")
	private byte[] privateKey;

	@Column(name = "master_key")
	private String masterAlias;

}
