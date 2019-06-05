package io.mosip.registration.processor.stages.osivalidator.utils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;

@Service
public class OSIUtils {

	/** The adapter. */
	@Autowired
	private FileSystemManager adapter;
	
	private IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
	
	public RegOsiDto getOSIDetailsFromMetaInfo(String registrationId,Identity identity) throws UnsupportedEncodingException {
		 
		RegOsiDto regOsi = new RegOsiDto();
		//regOsi.setIntroducerId("");// not found in json
		regOsi.setIntroducerTyp(getMetaDataValue(JsonConstant.INTRODUCERTYPE,identity));
		regOsi.setOfficerHashedPin(getMetaDataValue(JsonConstant.OFFICERPIN,identity));
		regOsi.setOfficerHashedPwd(getOsiDataValue(JsonConstant.OFFICERPWR,identity));
		regOsi.setOfficerId(getOsiDataValue(JsonConstant.OFFICERID,identity));
		regOsi.setOfficerOTPAuthentication(getOsiDataValue(JsonConstant.OFFICEROTPAUTHENTICATION,identity));
		regOsi.setPreregId(getMetaDataValue(JsonConstant.PREREGISTRATIONID,identity));
		regOsi.setRegId(getMetaDataValue(JsonConstant.REGISTRATIONID,identity));
		regOsi.setSupervisorBiometricFileName(getOsiDataValue(JsonConstant.SUPERVISORBIOMETRICFILENAME,identity));
		regOsi.setSupervisorHashedPin(getOsiDataValue(JsonConstant.OFFICERPHOTONAME,identity));
		regOsi.setSupervisorHashedPwd(getOsiDataValue(JsonConstant.SUPERVISORPWR,identity));
		regOsi.setSupervisorId(getOsiDataValue(JsonConstant.SUPERVISORID,identity));
		regOsi.setSupervisorOTPAuthentication(getOsiDataValue(JsonConstant.SUPERVISOROTPAUTHENTICATION,identity));
		regOsi.setOfficerBiometricFileName(getOsiDataValue(JsonConstant.OFFICERBIOMETRICFILENAME,identity));
		
		return regOsi;
	}
	
	public String getOsiDataValue(String label,Identity identity) throws UnsupportedEncodingException {
		List<FieldValue> osiData = identity.getOsiData();
		return identityIteratorUtil.getMetadataLabelValue(osiData, label);

	}
	public String getMetaDataValue(String label,Identity identity) throws UnsupportedEncodingException {
		List<FieldValue> metadata = identity.getMetaData();
		return identityIteratorUtil.getMetadataLabelValue(metadata, label);

	}
	
	public Identity getIdentity(String registrationId) throws PacketDecryptionFailureException, ApisResourceAccessException, IOException, java.io.IOException {
		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKET_META_INFO.name());
		PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,PacketMetaInfo.class);
		return packetMetaInfo.getIdentity();

	}

}