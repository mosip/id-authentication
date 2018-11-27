/**
 * 
 */
package io.mosip.kernel.keymanagerservice.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "alias_map", schema = "keymanager")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliasMap {

	@Id
	private String alias;

	private String applicationId;

	private String machineId;

	private LocalDateTime timeStamp;

}
