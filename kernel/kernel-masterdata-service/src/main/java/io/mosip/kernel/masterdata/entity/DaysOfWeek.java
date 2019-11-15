package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.WeekDayId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="daysofweek_list",schema="master")
@IdClass(WeekDayId.class)
public class DaysOfWeek extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5984896378379371310L;
	
	@Id
	@Column(name = "code", nullable = false, length = 3,insertable=false,updatable=false)
	private String code;
	
	@Id
	@Column(name="lang_code",nullable=false,length=3)
	private String langCode;
	
	@Column(name = "name", nullable = false, length = 36)
	private String name;
	
	@Column(name="day_seq",nullable=false)
	private short daySeq;
	
	@OneToMany(fetch=FetchType.EAGER)
	private List<RegWorkingNonWorking> workingDayEntity;
	

}
