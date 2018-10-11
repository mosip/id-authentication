package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Component
@Entity
@Getter@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="document",schema="document")
public class DocumentEntity {
	@Id
	private int documentId;
	
	private String preregId;
	
	private String doc_name;
	
	private String doc_cat_code;
	
	private String doc_typ_code;
	
	private String doc_file_format;
	
	private byte[] doc_store;
	
	private String status_code;
	
	private String lang_code;
	
	private String cr_by;
	
	private Timestamp cr_dtimesz;
	
	private String upd_by;
	
	private Timestamp upd_dtimesz;

}
