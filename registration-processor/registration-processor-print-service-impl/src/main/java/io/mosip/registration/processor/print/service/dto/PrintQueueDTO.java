package io.mosip.registration.processor.print.service.dto;

import java.io.Serializable;
import java.util.Arrays;

import lombok.Data;

@Data
public class PrintQueueDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private byte[] pdfBytes;
	
	private byte[] textBytes;
	
	private String uin;
	
	public byte[] getPdfBytes() {
        if(pdfBytes!=null)
               return Arrays.copyOf(pdfBytes, pdfBytes.length);
        return null;
  }
     public void setPdfBytes(byte[] pdfBytes) {
                     this.pdfBytes=pdfBytes!=null?pdfBytes:null;
               }

     public byte[] getTextBytes() {
         if(textBytes!=null)
                return Arrays.copyOf(textBytes, textBytes.length);
         return null;
   }
      public void setTextBytes(byte[] textBytes) {
                      this.textBytes=textBytes!=null?textBytes:null;
                }

}
