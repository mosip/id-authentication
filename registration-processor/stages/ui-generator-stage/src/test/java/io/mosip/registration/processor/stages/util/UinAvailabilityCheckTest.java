package io.mosip.registration.processor.stages.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.stages.uingenerator.util.UinAvailabilityCheck;


@RunWith(PowerMockRunner.class)
@PrepareForTest({JsonUtil.class})
public class UinAvailabilityCheckTest {

	@InjectMocks
	private UinAvailabilityCheck uinAvailabilityCheck;

	@Mock
	private InputStream inputStream;

	@Mock
	private  FileSystemAdapter<InputStream, Boolean> adapter;

	@Mock
	private PacketMetaInfo packetMetaInfo;

	@Mock
	private List<FieldValue> hashSequence;

	@Mock
	private Identity identity;

	@Mock
	private IdentityIteratorUtil identityIteratorUtil;

	@Before
	public void setup() throws Exception {
		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
		.thenReturn(packetMetaInfo);
		Mockito.when(packetMetaInfo.getIdentity()).thenReturn(identity);
		Mockito.when(identity.getMetaData()).thenReturn(hashSequence);
	}
	
	@Test
	public void uinCheckSuccess() {
		Mockito.when(identityIteratorUtil.getMetadataLabelValue(hashSequence,"uin")).thenReturn("12345");
		boolean result= uinAvailabilityCheck.uinCheck("12345", adapter);
		assertTrue(result);
	}
	
	@Test
	public void uinCheckFail() {
		Mockito.when(identityIteratorUtil.getMetadataLabelValue(hashSequence,"uin")).thenReturn(null);
		boolean result= uinAvailabilityCheck.uinCheck("12345", adapter);
		assertFalse(result);
	}

	@Test
	public void uinUnsupportedEncordingException() throws Exception {
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
		.thenThrow(new UnsupportedEncodingException( "json object parsing failed"));
		Mockito.when(identityIteratorUtil.getMetadataLabelValue(hashSequence,"uin")).thenReturn("12345");
		uinAvailabilityCheck.uinCheck("12345", adapter);
		
	}
}
