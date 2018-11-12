package io.mosip.kernel.keymanager.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

/**
 * The entity class for KeyAlias
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Entity
@Data

@Table(name = "key_manager", schema = "kernel")
public class KeyAlias{


}
