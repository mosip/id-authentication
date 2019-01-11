package io.mosip.registration.processor.stages.uingenerator.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;

public class UinAvailabilityCheck {

	IdentityIteratorUtil identityIteratorUtil =  new IdentityIteratorUtil();
	Boolean result=false;
	private static Logger regProcLogger = (Logger) RegProcessorLogger.getLogger(UinAvailabilityCheck.class);

	public boolean uinCheck(String registrationId,FileSystemAdapter<InputStream, Boolean> adapter) {
		InputStream packetMetaInfoStream = adapter.getFile(registrationId , PacketFiles.PACKETMETAINFO.name());

		try {
			PacketMetaInfo 	packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketMetaInfo.class);
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

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		    //	e.printStackTrace();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
				PlatformErrorMessages.UNSUPPORTED_ENCODING.name() + e.getMessage());
	//		adapter.isPacketPresent(registrationId);
		}

		return result;
	}

}
