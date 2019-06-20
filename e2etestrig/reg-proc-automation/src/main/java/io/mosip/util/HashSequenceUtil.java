package io.mosip.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import io.mosip.util.HMACUtils;



public class HashSequenceUtil {

	public HashSequenceUtil() {
		super();
		
	}
	
	public String getPacketHashSequence(File file) {
		InputStream encryptedInputStream;
		byte[] isbytearray;
		String hashSequence="";
		try {
			encryptedInputStream = new FileInputStream(file.getAbsolutePath());
			isbytearray = IOUtils.toByteArray(encryptedInputStream);
			HMACUtils.update(isbytearray);
			 hashSequence = HMACUtils.digestAsPlainText(HMACUtils.updatedHash());
			return hashSequence;
		} catch (FileNotFoundException e1) {
			
		
		}catch (IOException e) {
		
		}
	     return hashSequence;
	}
   public long getPacketSize(File file) {
	  return file.length();
	 
	
  }
}
