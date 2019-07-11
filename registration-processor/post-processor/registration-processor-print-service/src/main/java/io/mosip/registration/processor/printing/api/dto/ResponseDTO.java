package io.mosip.registration.processor.printing.api.dto;

import java.io.Serializable;
import java.util.Arrays;

import lombok.Data;

@Data
public class ResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] file;
	
	public byte[] getFile() {
        if(file!=null)
               return Arrays.copyOf(file, file.length);
        return null;
  }
                     public void setFile(byte[] file) {
                     this.file=file!=null?file:null;
               }

}
