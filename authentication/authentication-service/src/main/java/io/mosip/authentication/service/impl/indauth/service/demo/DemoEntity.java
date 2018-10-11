package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "indv_demographic", schema = "ida")
public class DemoEntity {

	//FIX ME annotate columns for all fields.
	@Id
	private String uinRefId;
	
	private String lang1Code;

	private String lang1Name;

    private String lang1Gender;

	private String lang1DobType;

	private String lang1Email;

	private Integer lang1Age;

	private Date lang1Dob;

	private String lang1Mobile;

	private String lang1AddressLine1;

	private String lang1AddressLine2;

	private String lang1AddressLine3;
	
	private String lang1LocLine1;
	
	private String lang1LocLine2;
	
	private String lang1LocLine3;
	
	private String lang1LocLine4;
	
	private String 	lang1Country;
	
	private String lang1PinCode;
	

		
}
