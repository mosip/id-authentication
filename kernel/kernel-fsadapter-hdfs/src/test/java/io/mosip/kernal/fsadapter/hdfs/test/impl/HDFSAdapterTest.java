package io.mosip.kernal.fsadapter.hdfs.test.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystem.Statistics;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernal.fsadapter.hdfs.test.util.SeekableByteArrayInputStream;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.fsadapter.hdfs.impl.HDFSAdapter;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtil;

@RunWith(SpringRunner.class)
public class HDFSAdapterTest {

	private FileSystem fs;
	private FSDataInputStream inStream;
	private FSDataOutputStream outStream;
	private HDFSAdapter hdfsAdapterImpl;
	private String id;
	private String filepath;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() throws IOException {
		id = "927479538402";
		filepath = "DEMOGRAPHIC/POA_PASSPORT";
		ConnectionUtil connectionUtil = Mockito.mock(ConnectionUtil.class);
		fs = Mockito.mock(FileSystem.class);
		ClassLoader classLoader = getClass().getClassLoader();
		String filePath = classLoader.getResource(id + ".zip").getFile();
		inStream = new FSDataInputStream(
				new SeekableByteArrayInputStream(FileUtils.readFileToByteArray(new File(filePath))));
		outStream = new FSDataOutputStream(FileUtils.openOutputStream(folder.newFile()), new Statistics("new"));
		hdfsAdapterImpl = new HDFSAdapter(connectionUtil);

		ReflectionTestUtils.setField(hdfsAdapterImpl, "fs", fs);
	}

	@Test
	public void checkFileExistenceTest() throws IOException {
		when(fs.exists(Mockito.any())).thenReturn(true);
		boolean res = hdfsAdapterImpl.checkFileExistence(id, filepath);
		assertThat(res, is(true));
	}

	@Test(expected = FSAdapterException.class)
	public void checkFileExistenceExceptionTest() throws IOException {
		when(fs.exists(Mockito.any())).thenThrow(new IOException());
		hdfsAdapterImpl.checkFileExistence(id, filepath);
	}

	@Test
	public void deleteFileTest() throws IOException {
		when(fs.delete(Mockito.any(Path.class), Mockito.anyBoolean())).thenReturn(true);
		boolean res = hdfsAdapterImpl.deleteFile(id, filepath);
		assertThat(res, is(true));
	}

	@Test(expected = FSAdapterException.class)
	public void deleteFileExceptionTest() throws IOException {
		when(fs.delete(Mockito.any(Path.class), Mockito.anyBoolean())).thenThrow(new IOException());
		hdfsAdapterImpl.deleteFile(id, filepath);
	}

	@Test
	public void deletePacketTest() throws IOException {
		when(fs.delete(Mockito.any(Path.class), Mockito.anyBoolean())).thenReturn(true);
		boolean res = hdfsAdapterImpl.deletePacket(id);
		assertThat(res, is(true));
	}

	@Test(expected = FSAdapterException.class)
	public void deletePacketExceptionTest() throws IOException {
		when(fs.delete(Mockito.any(Path.class), Mockito.anyBoolean())).thenThrow(new IOException());
		hdfsAdapterImpl.deletePacket(id);
	}

	@Test
	public void getFileTest() throws IOException {
		when(fs.exists(Mockito.any())).thenReturn(true);
		when(fs.open(Mockito.any())).thenReturn(inStream);
		InputStream res = hdfsAdapterImpl.getFile(id, filepath);
		assertThat(res, is(inStream));
	}

	@Test(expected = FSAdapterException.class)
	public void getFileExceptionTest() throws IOException {
		when(fs.exists(Mockito.any())).thenReturn(true);
		when(fs.open(Mockito.any())).thenThrow(new IOException());
		hdfsAdapterImpl.getFile(id, filepath);
	}

	@Test(expected = FSAdapterException.class)
	public void getFileNotFoundExceptionTest() throws IOException {
		when(fs.exists(Mockito.any())).thenReturn(false);
		hdfsAdapterImpl.getFile(id, filepath);
	}

	@Test
	public void getPacketTest() throws IOException {
		when(fs.exists(Mockito.any())).thenReturn(true);
		when(fs.open(Mockito.any())).thenReturn(inStream);
		InputStream res = hdfsAdapterImpl.getPacket(id);
		assertThat(res, is(inStream));
	}

	@Test(expected = FSAdapterException.class)
	public void getPacketExceptionTest() throws IOException {
		when(fs.exists(Mockito.any())).thenReturn(true);
		when(fs.open(Mockito.any())).thenThrow(new IOException());
		hdfsAdapterImpl.getPacket(id);
	}

	@Test(expected = FSAdapterException.class)
	public void getPacketNotFoundExceptionTest() throws IOException {
		when(fs.exists(Mockito.any())).thenReturn(false);
		hdfsAdapterImpl.getPacket(id);
	}

	@Test
	public void isPacketPresentTest() throws IOException {
		when(fs.exists(Mockito.any())).thenReturn(true);
		boolean res = hdfsAdapterImpl.isPacketPresent(id);
		assertThat(res, is(true));
	}

	@Test(expected = FSAdapterException.class)
	public void isPacketPresentExceptionTest() throws IOException {
		when(fs.exists(Mockito.any())).thenThrow(new IOException());
		hdfsAdapterImpl.isPacketPresent(id);
	}

	@Test
	public void storeFileTest() throws IOException {
		when(fs.create(Mockito.any(Path.class))).thenReturn(outStream);
		boolean res = hdfsAdapterImpl.storeFile(id, filepath, inStream);
		assertThat(res, is(true));
	}

	@Test(expected = FSAdapterException.class)
	public void storeFileExceptionTest() throws IOException {
		when(fs.create(Mockito.any(Path.class))).thenThrow(new IOException());
		hdfsAdapterImpl.storeFile(id, filepath, inStream);
	}

	@Test
	public void storePacketTest() throws IOException {
		when(fs.create(Mockito.any(Path.class))).thenReturn(outStream);
		boolean res = hdfsAdapterImpl.storePacket(id, inStream);
		assertThat(res, is(true));
	}

	@Test(expected = FSAdapterException.class)
	public void storePacketExceptionTest() throws IOException {
		when(fs.create(Mockito.any(Path.class))).thenThrow(new IOException());
		hdfsAdapterImpl.storePacket(id, inStream);
	}

	@Test
	public void storePacketFileTest() throws IOException {
		File createdFile = folder.newFile();
		when(fs.create(Mockito.any(Path.class))).thenReturn(outStream);
		boolean res = hdfsAdapterImpl.storePacket(id, createdFile);
		assertThat(res, is(true));
	}

	@Test(expected = FSAdapterException.class)
	public void storePacketFileExceptionTest() throws IOException {
		File createdFile = folder.newFile();
		when(fs.create(Mockito.any(Path.class))).thenThrow(new IOException());
		hdfsAdapterImpl.storePacket(id, createdFile);
	}

	@Test
	public void unpackPacketTest() throws IOException {
		when(fs.exists(Mockito.any())).thenReturn(true);
		when(fs.open(Mockito.any())).thenReturn(inStream);
		when(fs.create(Mockito.any(Path.class))).thenReturn(outStream);
		hdfsAdapterImpl.unpackPacket(id);
		verify(fs, atLeastOnce()).exists(Mockito.any());
		verify(fs, atLeastOnce()).open(Mockito.any());
		verify(fs, atLeastOnce()).create(Mockito.any(Path.class));
	}
}