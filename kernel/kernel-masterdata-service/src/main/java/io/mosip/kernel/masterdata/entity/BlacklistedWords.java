package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * blacklisted word entity
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blacklisted_words", schema = "master")
public class BlacklistedWords extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -402658536057675404L;

	@Id
	@Column(name = "word", nullable = false, length = 128)
	private String word;

	@Column(name = "descr", length = 256)
	private String description;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

}
