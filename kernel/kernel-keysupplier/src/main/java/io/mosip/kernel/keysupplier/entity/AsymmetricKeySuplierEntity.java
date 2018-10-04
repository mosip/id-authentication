package io.mosip.kernel.keysupplier.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "asymmetrickeys", schema = "keys")
@Data
public class AsymmetricKeySuplierEntity {
@Id
@Column(name="key_id",unique=true,nullable = false,updatable = false)
private long keyId;
@Column(name="application_id")
private String applicationId;
@Lob
@Column(name="private_key")
private byte[] privateKey;
@Lob
@Column(name="public_key")
private byte[] publicKey;
@Column(name="generation_time")
private LocalDateTime generationTime;
@Column(name="validity")
private LocalDateTime validity;
}
