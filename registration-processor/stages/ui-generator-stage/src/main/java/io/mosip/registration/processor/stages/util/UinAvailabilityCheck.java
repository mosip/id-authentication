package io.mosip.registration.processor.stages.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;

public class UinAvailabilityCheck {
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;
	
	public boolean uinCheck(String registrationId) {
		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKETMETAINFO.name());
		try {
			PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketMetaInfo.class);
			Identity identity = packetMetaInfo.getIdentity();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
