package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;

//github.com/mosip/mosip.git

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.kernel.syncdata.entity.id.HolidayID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 24-10-2018
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loc_holiday", schema = "master")
public class Holiday extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1329042436883315822L;

	@EmbeddedId
	private HolidayID holidayId;

	@Column(name = "id", nullable = false)
	private int id;

	@Column(name = "holiday_desc", length = 128)
	private String holidayDesc;

}
