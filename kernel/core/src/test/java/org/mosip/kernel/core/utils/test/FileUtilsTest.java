package org.mosip.kernel.core.utils.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;

import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mosip.kernel.core.utils.FileUtil;
import org.mosip.kernel.core.utils.exception.MosipFileExistsException;
import org.mosip.kernel.core.utils.exception.MosipFileNotFoundException;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.exception.MosipIllegalArgumentException;
import org.mosip.kernel.core.utils.exception.MosipNullPointerException;
import org.mosip.kernel.core.utils.exception.MosipUnsupportedCharsetException;

/**
 * @author Priya Soni
 *
 */
public class FileUtilsTest {
	FileUtil fileutils;
	
	

	@Test
	public void byteCountToDisplaySizeTest() {
		long size = 250000000l;
		assertThat(FileUtil.byteCountToDisplaySize(size), is("238 MB"));

	}

	///////////////////////////////////////////////////////////

	@Test(expected = MosipNullPointerException.class)
	public void checksumNullPOinterTest() throws MosipIOException {

		FileUtil.checksum(null, null);

	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test(expected = MosipIllegalArgumentException.class)
	public void checksumIllegalArgTest() throws MosipIOException {

		File directory;
		try {
			directory = folder.newFolder("sampleDirectory");

		} catch (IOException e) {
			throw new MosipIOException(null, null, e.getCause());
		}
		FileUtil.checksum(directory, new CRC32());

	}

	@Test(expected = MosipIOException.class)
	public void checksumIOTest() throws MosipIOException {

		File file = new File("");
		FileUtil.checksum(file, new CRC32());

	}

	@Test
	public void checksumTest() throws MosipIOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			long cksm = FileUtil.checksum(file, new CRC32()).getValue();
			assertEquals(0, cksm);
		} catch (IOException e) {
			throw new MosipIOException(null, null);
		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void checksumCRC32Test() throws MosipIOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			long cksm = FileUtil.checksumCRC32(file);
			assertEquals(0, cksm);
		} catch (IOException e) {
			throw new MosipIOException(null, null);
		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void checksumCRC32NullPOinterTest() throws MosipIOException {

		FileUtil.checksumCRC32(null);

	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void checksumCRC32IllegalArgTest() throws MosipIOException {

		File directory;
		try {
			directory = folder.newFolder("sampleDirectory");

		} catch (IOException e) {
			throw new MosipIOException(null, null, e.getCause());
		}
		FileUtil.checksumCRC32(directory);

	}

	@Test(expected = MosipIOException.class)
	public void checksumCRC32IOTest() throws MosipIOException {

		File file = new File("");
		FileUtil.checksumCRC32(file);

	}

	///////////////////////////////////////////////////////////

	@Test(expected = MosipIllegalArgumentException.class)
	public void cleanDirectoryIllegalArgTest() throws MosipIOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			FileUtil.cleanDirectory(file);
		} catch (IOException e) {
			throw new MosipIOException(null, null, e.getCause());
		}

	}

	@Test
	public void cleanDirectoryTest() throws MosipIOException {

		File directory;
		try {
			directory = folder.newFolder("sampleDir");
			FileUtil.cleanDirectory(directory);
		} catch (IOException e) {
			throw new MosipIOException(null, null, e.getCause());
		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void contentEqualsTest() throws MosipIOException {

		try {
			File file1 = folder.newFile("file1.txt");
			File file2 = folder.newFile("file2.txt");
			assertThat(FileUtil.contentEquals(file1, file2), is(true));
		} catch (IOException e) {
			throw new MosipIOException(null, null, e.getCause());
		}

	}

	@Test(expected = MosipIOException.class)
	public void contentEqualsIOTest() throws MosipIOException {
		try {
			File file1 = folder.newFolder("file1.txt");
			File file2 = folder.newFolder("file2.txt");
			assertThat(FileUtil.contentEquals(file1, file2), is(false));
		} catch (IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void contentEqualsIgnoreEOLTest() throws MosipIOException {
		try {
			File file1 = folder.newFile("file1.txt");
			File file2 = folder.newFile("file2.txt");
			assertThat(FileUtil.contentEqualsIgnoreEOL(file1, file2, null), is(true));
		} catch (IOException e) {
			throw new MosipIOException("", "", e.getCause());
		}
	}

	@Test(expected = MosipIOException.class)
	public void contentEqualsIgnoreEOLIOTest() throws MosipIOException {

		File file1;
		try {
			file1 = folder.newFolder("abc");
			File file2 = folder.newFolder("def");
			FileUtil.contentEqualsIgnoreEOL(file1, file2, null);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void convertFileCollectionToFileArrayTest() throws MosipIOException {
		File[] fileArray = new File[2];

		try {
			File file1 = folder.newFile("file1.txt");
			File file2 = folder.newFile("file2.txt");
			fileArray[0] = file1;
			fileArray[1] = file2;
			List<File> listFile = new ArrayList<File>();
			listFile.add(file1);
			listFile.add(file2);

			assertTrue(Arrays.equals(FileUtil.convertFileCollectionToFileArray(listFile), fileArray));

		} catch (IOException e) {
			throw new MosipIOException(null, null, e.getCause());
		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void copyDirectoryTest() throws MosipIOException {
		try {
			File dir1 = folder.newFolder("dir1");
			File dir2 = folder.newFolder("dir2");
			FileUtil.copyDirectory(dir1, dir2);
		} catch (IOException e) {
			throw new MosipIOException(null, null, e.getCause());
		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void copyDirectoryNullPTest() throws MosipIOException {
		try {
			File dir1 = folder.newFolder("dir1");
			File dir2 = folder.newFolder("dir2");
			dir1 = null;
			FileUtil.copyDirectory(dir1, dir2);
		} catch (IOException e) {
			throw new MosipIOException(null, null, e.getCause());
		}
	}

	@Test(expected = MosipIOException.class)
	public void copyDirectoryIOTest() throws MosipIOException {

		File dir1 = new File("");
		File dir2 = new File("");
		FileUtil.copyDirectory(dir1, dir2);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void copyFileTest() throws MosipIOException {
		try {
			File file1 = folder.newFile("file1.txt");
			File file2 = folder.newFile("file2.txt");
			FileUtil.copyFile(file1, file2);

		} catch (IOException e) {

		}

	}

	@Test(expected = MosipNullPointerException.class)
	public void copyFileNullPTest() throws MosipIOException {
		try {
			File file1 = folder.newFolder("file1");
			File file2 = folder.newFolder("file2");
			file1 = null;
			FileUtil.copyFile(file1, file2);
		} catch (IOException e) {
			throw new MosipIOException(null, null, e.getCause());
		}
	}

	@Test(expected = MosipIOException.class)
	public void copyFileIOTest() throws MosipIOException {
		File file1 = new File("");
		File file2 = new File("");
		FileUtil.copyFile(file1, file2);
	}

	///////////////////////////////////////////////////////////

	@Test
	public void copyFileStreamTest() throws MosipIOException {
		try {
			File file = folder.newFile("file1.txt");
			OutputStream os = new OutputStream() {

				@Override
				public void write(int b) throws IOException {
					// do nothing

				}
			};

			FileUtil.copyFile(file, os);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipNullPointerException.class)
	public void copyFileStreamNullPTest() throws MosipIOException {
		try {
			File file = folder.newFile("file.txt");
			OutputStream os = new OutputStream() {

				@Override
				public void write(int b) throws IOException {
					// do nothing

				}
			};
			file = null;
			FileUtil.copyFile(file, os);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void copyFileStreamIOTest() throws MosipIOException {

		File file = new File("");
		OutputStream os = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				// do nothing

			}
		};

		FileUtil.copyFile(file, os);

	}

	///////////////////////////////////////////////////////////

	@Test(expected = MosipIOException.class)
	public void copyInputStreamToFileIOTest() throws MosipIOException {

		InputStream istream = new InputStream() {

			@Override
			public int read() throws IOException {
				return 0;
			}
		};
		File file1 = new File("");
		FileUtil.copyInputStreamToFile(istream, file1);

	}

	///////////////////////////////////////////////////////////

	@Test(expected = MosipIOException.class)
	public void copyToFileIOTest() throws MosipIOException {

		InputStream istream = new InputStream() {

			@Override
			public int read() throws IOException {
				return 0;
			}
		};
		File file1;
		try {
			file1 = folder.newFolder("dir");
			FileUtil.copyToFile(istream, file1);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void deleteDirectoryTest() throws MosipIOException {
		try {
			File dir = folder.newFolder("dir");
			FileUtil.deleteDirectory(dir);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void deleteDirectoryIllegalArgTest() throws MosipIOException {
		try {
			File dir = folder.newFile("dir");
			FileUtil.deleteDirectory(dir);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void deleteQuietlyTest() throws MosipIOException {
		try {
			File dir = folder.newFile("dir");
			FileUtil.deleteQuietly(dir);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void directoryContainsTest() throws MosipIOException {
		try {
			File dir = folder.newFolder("parentDir");
			File file = dir.getParentFile();
			assertFalse(FileUtil.directoryContains(dir, file));
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void directoryContainsIllegalArgTest() throws MosipIOException {

		File dir;
		try {
			dir = folder.newFile("sampleFile.txt");
			File file = dir.getParentFile();
			assertFalse(FileUtil.directoryContains(dir, file));
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void forceDeleteTest() throws MosipIOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			FileUtil.forceDelete(file);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void forceDeleteNullPTest() throws MosipIOException {

		File file = null;
		FileUtil.forceDelete(file);

	}

	@Test(expected = MosipFileNotFoundException.class)
	public void forceDeleteFileNFTest() throws MosipIOException {

		File file = new File("sampleFile");
		FileUtil.forceDelete(file);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void forceDeleteOnExitTest() throws MosipIOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			FileUtil.forceDeleteOnExit(file);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void forceDeleteOnExitNullPTest() throws MosipIOException {

		File file = null;
		FileUtil.forceDeleteOnExit(file);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void getFileWithDirTest() {
		try {
			File dir = folder.newFolder("dir");
			String[] names = { "file1.txt", "file2.txt" };
			FileUtil.getFile(dir, names);
		} catch (IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void getFileTest() {
		String[] names = { "file1.txt", "file2.txt" };
		FileUtil.getFile(names);
	}

	///////////////////////////////////////////////////////////

	@Test
	public void isFileNewerTest() {
		try {
			File file = folder.newFile("sampleFile.txt");
			Date date = new Date(10000);
			assertTrue(FileUtil.isFileNewer(file, date));
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void isFileNewerIllegalArgTest() {

		File file = null;
		Date date = new Date(10000);
		FileUtil.isFileNewer(file, date);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void isFileOlderTest() {
		try {
			File file = folder.newFile("sampleFile.txt");
			Date date = new Date(10000);
			assertFalse(FileUtil.isFileOlder(file, date));
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void isFileOlderIllegalArgTest() {

		File file = null;
		Date date = new Date(10000);
		FileUtil.isFileOlder(file, date);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void isSymlinkTest() throws MosipIOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			assertFalse(FileUtil.isSymlink(file));
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void isSymlinkNullPTest() throws MosipIOException {

		File file = null;
		assertFalse(FileUtil.isSymlink(file));

	}

	///////////////////////////////////////////////////////////

	@Test
	public void iterateFilesTest() {
		try {
			File directory = folder.newFolder("sampleFolder");
			Iterator<File> fileIt = FileUtil.iterateFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			fileIt.hasNext();
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void lineIteratorTest() throws MosipIOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			LineIterator lItr = FileUtil.lineIterator(file);
			lItr.hasNext();
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void lineIteratorIOTest() throws MosipIOException {

		File file = new File("");
		LineIterator lItr = FileUtil.lineIterator(file);
		lItr.hasNext();

	}

	///////////////////////////////////////////////////////////

	@Test
	public void lineIteratorWithEncodingTest() throws MosipIOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			LineIterator lItr = FileUtil.lineIterator(file, "UTF-8");
			lItr.hasNext();
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void lineIteratorWithEncodingIOTest() throws MosipIOException {

		File file = new File("");
		LineIterator lItr = FileUtil.lineIterator(file, "UTF-8");
		lItr.hasNext();

	}

	///////////////////////////////////////////////////////////

	@Test
	public void listFilesTest() {

		File directory;
		try {
			directory = folder.newFolder("directory");
			FileUtil.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void listFilesAndDirsTest() {

		File directory;
		try {
			directory = folder.newFolder("directory");
			FileUtil.listFilesAndDirs(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test(expected = MosipFileExistsException.class)
	public void moveDirectoryFileExistsTest() throws MosipIOException {
		try {
			File dir1 = folder.newFolder("dir1");
			File dir2 = folder.newFolder("dir2");
			FileUtil.moveDirectory(dir1, dir2);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void moveDirectoryIOTest() throws MosipIOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = new File("");
			FileUtil.moveDirectory(dirSource, dirDest);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void moveDirectoryNullPTest() throws MosipIOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = null;
			FileUtil.moveDirectory(dirSource, dirDest);
		} catch (IOException e) {

		}
	}

	@Test
	public void moveDirectoryTest() throws MosipIOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = new File("moveDirSampleFolder");
			FileUtil.moveDirectory(dirSource, dirDest);
			FileUtil.deleteQuietly(dirDest);
		} catch (IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test(expected = MosipIOException.class)
	public void moveDirectoryToDirectoryIOTest() throws MosipIOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = new File("sampleFolder");
			FileUtil.moveDirectoryToDirectory(dirSource, dirDest, false);

		} catch (IOException e) {

		}
	}

	@Test
	public void moveDirectoryToDirectoryTest() throws MosipIOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = folder.newFolder("sampleFolder");
			FileUtil.moveDirectoryToDirectory(dirSource, dirDest, false);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void moveDirectoryToDirectoryNullPTest() throws MosipIOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = null;
			FileUtil.moveDirectoryToDirectory(dirSource, dirDest, false);
		} catch (IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test(expected = MosipFileExistsException.class)
	public void moveFileFileExistsTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile");
			File destFile = folder.newFile("destFile");
			FileUtil.moveFile(srcFile, destFile);
		} catch (IOException e) {

		}
	}

	@Test
	public void moveFileTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destFile = new File("moveFileDestFile.txt");
			FileUtil.moveFile(srcFile, destFile);
			FileUtil.deleteQuietly(destFile);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void moveFileNullPTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destFile = null;
			FileUtil.moveFile(srcFile, destFile);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void moveFileIOTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile");
			File destFile = new File("");
			FileUtil.moveFile(srcFile, destFile);
		} catch (IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void moveFileToDirectoryTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = folder.newFolder("moveFileToDirectoryDestDir");
			FileUtil.moveFileToDirectory(srcFile, destDir, false);
			FileUtil.deleteQuietly(destDir);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void moveFileToDirectoryNullPTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = null;
			FileUtil.moveFileToDirectory(srcFile, destDir, false);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void moveFileToDirectoryIOTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile");
			File destDir = new File("");
			FileUtil.moveFileToDirectory(srcFile, destDir, false);
		} catch (IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void moveToDirectoryTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = folder.newFolder("moveToDirectoryDestDir");
			FileUtil.moveToDirectory(srcFile, destDir, false);
			FileUtil.deleteQuietly(destDir);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void moveToDirectoryIOTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = new File("");
			FileUtil.moveToDirectory(srcFile, destDir, false);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipNullPointerException.class)
	public void moveToDirectoryNullPTest() throws MosipIOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = null;
			FileUtil.moveToDirectory(srcFile, destDir, false);

		} catch (IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void openInputStreamTest() throws MosipIOException {

		try {

			File file = folder.newFile();
			FileUtil.openInputStream(file);
		
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipFileNotFoundException.class)
	public void openInputStreamFileNFTest() throws MosipIOException {

		File file = new File("openIPStramfile.txt");
		FileUtil.openInputStream(file);

	}

	@Test(expected = MosipIOException.class)
	public void openInputStreamIOTest() throws MosipIOException {

		File file;
		try {
			file = folder.newFolder();
			FileUtil.openInputStream(file);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void openOutputStreamTest() throws MosipIOException {
		
		try {
			File file = folder.newFile();
			FileUtil.openOutputStream(file);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void openOutputStreamIOTest() throws MosipIOException {

		File file;
		try {
			file = folder.newFolder();
			FileUtil.openOutputStream(file);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void openOutputStreamWithAppendTest() throws MosipIOException {
		
		try {
			File file = folder.newFile();
			FileUtil.openOutputStream(file, true);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void openOutputStreamWithAppendIOTest() throws MosipIOException {

		File file;
		try {
			file = folder.newFolder();
			FileUtil.openOutputStream(file, true);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void readFileToByteArrayTest() throws MosipIOException {

		try {
			File file = folder.newFile();
			FileUtil.readFileToByteArray(file);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void readFileToByteArrayIOTest() throws MosipIOException {
		File file = new File("");
		FileUtil.readFileToByteArray(file);
	}

	///////////////////////////////////////////////////////////

	@Test
	public void readFileToStringTest() throws MosipIOException {

		try {
			File file = folder.newFile();
			FileUtil.readFileToString(file, null);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void readFileToStringIOTest() throws MosipIOException {
		File file = new File("");
		FileUtil.readFileToString(file, null);
	}

	///////////////////////////////////////////////////////////

	@Test
	public void readLinesTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			FileUtil.readLines(file, Charset.forName("UTF-8"));
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void readLinesIOTest() throws MosipIOException {

		try {
			File file = folder.newFolder();
			FileUtil.readLines(file, Charset.forName("UTF-8"));
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void readLinesWithStringEncodingTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			FileUtil.readLines(file, "UTF-8");
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipUnsupportedCharsetException.class)
	public void readLinesWithStringEncodingUCharsetTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			FileUtil.readLines(file, "UT");
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void readLinesWithStringEncodingIOTest() throws MosipIOException {
		try {
			File file = folder.newFolder();
			FileUtil.readLines(file, "UTF-8");
		} catch (IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void sizeOfTest() {
		try {
			File file = folder.newFile();
			assertEquals(0,FileUtil.sizeOf(file));
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipNullPointerException.class)
	public void sizeOfNullPTest() {

		File file = null;
		assertEquals(0,FileUtil.sizeOf(file));

	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void sizeOfIllegalArgTest() {

		File file = new File("");
		assertEquals(0,FileUtil.sizeOf(file));

	}

	///////////////////////////////////////////////////////////

	@Test
	public void sizeOfDirectoryTest() {
		try {
			File dir = folder.newFolder();
			assertEquals(0,FileUtil.sizeOfDirectory(dir));
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipNullPointerException.class)
	public void sizeOfDirectoryNullPTest() {

		File dir = null;
		assertEquals(0,FileUtil.sizeOfDirectory(dir));

	}

	///////////////////////////////////////////////////////////

	@Test
	public void toFileTest() {
		try {
		 FileUtil.toFile(new URL("http://www.example.com/docs/resource1.html"));
		} catch (MalformedURLException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test(expected = MosipIllegalArgumentException.class)
	public void toFilesTest() {

		try {
			URL[] urls = new URL[2];
			URL url1 = new URL("http://www.example.com/docs/resource1.ht");
			URL url2 = new URL("http://www.example.com/docs/resource1.ht");
			urls[0] = url1;
			urls[1] = url2;
			File[] file = FileUtil.toFiles(urls);
			assertThat(file, null);
		} catch (MalformedURLException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void toURLsTest() throws MosipIOException {
		try {
			File file1 = folder.newFile();
			File file2 = folder.newFile();
			File[] fileArray = { file1, file2 };
			FileUtil.toURLs(fileArray);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipNullPointerException.class)
	public void toURLsNullPTest() throws MosipIOException {
		try {
			File file1 = folder.newFile();
			File file2 = null;
			File[] fileArray = { file1, file2 };
			FileUtil.toURLs(fileArray);
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test(expected = MosipNullPointerException.class)
	public void waitForNullPTest() {

		File file = null;
		assertFalse(FileUtil.waitFor(file, 2));

	}

	@Test
	public void waitForTest() {

		File file;
		try {
			file = folder.newFile("waitForFile.txt");
			assertTrue(FileUtil.waitFor(file, 2));
		} catch (IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeWithStringEncTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			FileUtil.write(file, "", "UTF-8");
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeWithStringEncIOTest() throws MosipIOException {

		File file = new File("");
		FileUtil.write(file, "", "UTF-8");

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeWithCharsetEncTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			FileUtil.write(file, "", Charset.defaultCharset());
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeWithCharsetEncIOTest() throws MosipIOException {

		File file = new File("");
		FileUtil.write(file, "", Charset.defaultCharset());

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeWithCharsetEncAndAppendTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			FileUtil.write(file, "", Charset.defaultCharset(), false);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeWithCharsetEncAndAppendIOTest() throws MosipIOException {

		File file = new File("");
		FileUtil.write(file, "", Charset.defaultCharset(), false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeWithStringEncAndAppendTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			FileUtil.write(file, "", "UTF-8", false);
		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeWithStringEncAndAppendIOTest() throws MosipIOException {

		File file = new File("");
		FileUtil.write(file, "", "UTF-8", false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeByteArrayToFileTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			byte[] data = new byte[2];
			FileUtil.writeByteArrayToFile(file, data);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void writeByteArrayToFileIOTest() throws MosipIOException {

		File file = new File("");
		byte[] data = new byte[2];
		FileUtil.writeByteArrayToFile(file, data);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeByteArrayToFileWithAppendTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			byte[] data = new byte[2];
			FileUtil.writeByteArrayToFile(file, data, false);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void writeByteArrayToFileWithAppendIOTest() throws MosipIOException {

		File file = new File("");
		byte[] data = new byte[2];
		FileUtil.writeByteArrayToFile(file, data, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeByteArrayToFileWithOffsetTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			byte[] data = new byte[2];
			FileUtil.writeByteArrayToFile(file, data, 0, 1);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void writeByteArrayToFileWithOffsetIOTest() throws MosipIOException {

		File file = new File("");
		byte[] data = new byte[2];
		FileUtil.writeByteArrayToFile(file, data, 0, 1);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeByteArrayToFileWithOffsetAndAppendTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			byte[] data = new byte[2];
			FileUtil.writeByteArrayToFile(file, data, 0, 1, false);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void writeByteArrayToFileWithOffsetAndAppendIOTest() throws MosipIOException {

		File file = new File("");
		byte[] data = new byte[2];
		FileUtil.writeByteArrayToFile(file, data, 0, 1, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtil.writeLines(file, lines);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeLinesIOTest() throws MosipIOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtil.writeLines(file, lines);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithAppendTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtil.writeLines(file, lines, false);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeLinesWithAppendIOTest() throws MosipIOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtil.writeLines(file, lines, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithAppendAndLineEndingAndEncTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtil.writeLines(file, "UTF-8", lines, null, false);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeLinesWithAppendAndLineEndingAndEncIOTest() throws MosipIOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtil.writeLines(file, "UTF-8", lines, null, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithLineEndingTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtil.writeLines(file, lines, null);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeLinesWithLineEndingIOTest() throws MosipIOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtil.writeLines(file, lines, null);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithLineEndingAndEncTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtil.writeLines(file, "UTF-8", lines, null);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeLinesWithLineEndingAndEncIOTest() throws MosipIOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtil.writeLines(file, "UTF-8", lines, null);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithEncTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtil.writeLines(file, "UTF-8", lines);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeLinesWithEncIOTest() throws MosipIOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtil.writeLines(file, "UTF-8", lines);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithEncAndAppendTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtil.writeLines(file, "UTF-8", lines, false);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeLinesWithEncAndAppendIOTest() throws MosipIOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtil.writeLines(file, "UTF-8", lines, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithLineEndingAndAppendTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtil.writeLines(file, lines, null, false);

		} catch (IOException e) {

		}
	}

	@Test(expected = MosipIOException.class)
	public void writeLinesWithLineEndingAndAppendIOTest() throws MosipIOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtil.writeLines(file, lines, null, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeStringToFileTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			String data = "sampleData";
			FileUtil.writeStringToFile(file, data, Charset.defaultCharset());
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void writeStringToFileIOTest() throws MosipIOException {

		File file = new File("");
		String data = "sampleData";
		FileUtil.writeStringToFile(file, data, Charset.defaultCharset());

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeStringToFileWithStringEncAndAppendTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			String data = "sampleData";
			FileUtil.writeStringToFile(file, data, "UTF-8", false);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void writeStringToFileWithStringEncAndAppendIOTest() throws MosipIOException {

		File file = new File("");
		String data = "sampleData";
		FileUtil.writeStringToFile(file, data, "UTF-8", false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeStringToFileWithStringEncTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			String data = "sampleData";
			FileUtil.writeStringToFile(file, data, "UTF-8");
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void writeStringToFileWithStringEncIOTest() throws MosipIOException {

		File file = new File("");
		String data = "sampleData";
		FileUtil.writeStringToFile(file, data, "UTF-8");

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeStringToFileWithAppendTest() throws MosipIOException {
		try {
			File file = folder.newFile();
			String data = "sampleData";
			FileUtil.writeStringToFile(file, data, Charset.defaultCharset(), false);
		} catch (IOException e) {

		}

	}

	@Test(expected = MosipIOException.class)
	public void writeStringToFileWithAppendIOTest() throws MosipIOException {

		File file = new File("");
		String data = "sampleData";
		FileUtil.writeStringToFile(file, data, Charset.defaultCharset(), false);

	}

}
