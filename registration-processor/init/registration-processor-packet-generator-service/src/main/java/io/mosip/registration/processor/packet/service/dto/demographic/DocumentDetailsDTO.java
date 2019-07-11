package io.mosip.registration.processor.packet.service.dto.demographic;

import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.mosip.registration.processor.packet.service.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class used to capture the Documents' details of the Individual
 * 
 * @author Sowmya
 * @since 1.0.0
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentDetailsDTO extends BaseDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1475892945577843287L;
	@JsonIgnore
	private byte[] document;
	protected String value;
	protected String type;
	@JsonIgnore
	protected String owner;
	protected String format;
	
	public byte[] getDocument() {
        if(document!=null)
               return Arrays.copyOf(document, document.length);
        return null;
  }
                     public void setDocument(byte[] document) {
                     this.document=document!=null?document:null;
               }


}
