package io.mosip.registration.processor.stages.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;

public class UinAvailabilityCheck {
//	@Autowired
//	private FileSystemAdapter<InputStream, Boolean> adapter;
	
	//FileSystemAdapter<InputStream, Boolean> adapter = new FilesystemCephAdapterImpl();
	IdentityIteratorUtil identityIteratorUtil =  new IdentityIteratorUtil();
	Boolean result=false;
	public boolean uinCheck(String registrationId,PacketMetaInfo packetMetaInfo) {
	//	InputStream packetMetaInfoStream = adapter.getFile("27847657360002520181208094032" , PacketFiles.PACKETMETAINFO.name());
		
			//PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
			//		PacketMetaInfo.class);
			Identity identity = packetMetaInfo.getIdentity();
			List<FieldValue> hashSequence = identity.getMetaData();
			String uinFieldCheck=identityIteratorUtil.getMetadataLabelValue(hashSequence, "uin");
			if(uinFieldCheck==null || uinFieldCheck.length()==0) {
				result= false;
				System.out.println("Debug output = False");
			}
			else {
				result=true;
				System.out.println("Debug output = True");

			}
				
	
		
		return result;
	}
	
}
