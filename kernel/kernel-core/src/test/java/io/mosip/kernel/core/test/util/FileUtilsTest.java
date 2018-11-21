package io.mosip.kernel.core.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

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

import io.mosip.kernel.core.exception.FileExistsException;
import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.NullPointerException;
import io.mosip.kernel.core.exception.UnsupportedCharsetException;
import io.mosip.kernel.core.util.FileUtils;

/**
 * @author Priya Soni
 *
 */
public class FileUtilsTest {
	FileUtils fileutils;

	@Test
	public void byteCountToDisplaySizeTest() {
		long size = 250000000l;
		assertThat(FileUtils.byteCountToDisplaySize(size), is("238 MB"));

	}

	///////////////////////////////////////////////////////////

	@Test(expected = NullPointerException.class)
	public void checksumNullPOinterTest() throws IOException {

		FileUtils.checksum(null, null);

	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test(expected = IllegalArgumentException.class)
	public void checksumIllegalArgTest() throws IOException {

		File directory;
		try {
			directory = folder.newFolder("sampleDirectory");

		} catch (java.io.IOException e) {
			throw new IOException(null, null, e.getCause());
		}
		FileUtils.checksum(directory, new CRC32());

	}

	@Test(expected = IOException.class)
	public void checksumIOTest() throws IOException {

		File file = new File("");
		FileUtils.checksum(file, new CRC32());

	}

	@Test
	public void checksumTest() throws IOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			long cksm = FileUtils.checksum(file, new CRC32()).getValue();
			assertEquals(0, cksm);
		} catch (java.io.IOException e) {
			throw new IOException(null, null);
		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void checksumCRC32Test() throws IOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			long cksm = FileUtils.checksumCRC32(file);
			assertEquals(0, cksm);
		} catch (java.io.IOException e) {
			throw new IOException(null, null);
		}
	}

	@Test(expected = NullPointerException.class)
	public void checksumCRC32NullPOinterTest() throws IOException {

		FileUtils.checksumCRC32(null);

	}

	@Test(expected = IllegalArgumentException.class)
	public void checksumCRC32IllegalArgTest() throws IOException {

		File directory;
		try {
			directory = folder.newFolder("sampleDirectory");

		} catch (java.io.IOException e) {
			throw new IOException(null, null, e.getCause());
		}
		FileUtils.checksumCRC32(directory);

	}

	@Test(expected = IOException.class)
	public void checksumCRC32IOTest() throws IOException {

		File file = new File("");
		FileUtils.checksumCRC32(file);

	}

	///////////////////////////////////////////////////////////

	@Test(expected = IllegalArgumentException.class)
	public void cleanDirectoryIllegalArgTest() throws IOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			FileUtils.cleanDirectory(file);
		} catch (java.io.IOException e) {
			throw new IOException(null, null, e.getCause());
		}

	}

	@Test
	public void cleanDirectoryTest() throws IOException {

		File directory;
		try {
			directory = folder.newFolder("sampleDir");
			FileUtils.cleanDirectory(directory);
		} catch (java.io.IOException e) {
			throw new IOException(null, null, e.getCause());
		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void contentEqualsTest() throws IOException {

		try {
			File file1 = folder.newFile("file1.txt");
			File file2 = folder.newFile("file2.txt");
			assertThat(FileUtils.contentEquals(file1, file2), is(true));
		} catch (java.io.IOException e) {
			throw new IOException(null, null, e.getCause());
		}

	}

	@Test(expected = IOException.class)
	public void contentEqualsIOTest() throws IOException {
		try {
			File file1 = folder.newFolder("file1.txt");
			File file2 = folder.newFolder("file2.txt");
			assertThat(FileUtils.contentEquals(file1, file2), is(false));
		} catch (java.io.IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void contentEqualsIgnoreEOLTest() throws IOException {
		try {
			File file1 = folder.newFile("file1.txt");
			File file2 = folder.newFile("file2.txt");
			assertThat(FileUtils.contentEqualsIgnoreEOL(file1, file2, null), is(true));
		} catch (java.io.IOException e) {
			throw new IOException("", "", e.getCause());
		}
	}

	@Test(expected = IOException.class)
	public void contentEqualsIgnoreEOLIOTest() throws IOException {

		File file1;
		try {
			file1 = folder.newFolder("abc");
			File file2 = folder.newFolder("def");
			FileUtils.contentEqualsIgnoreEOL(file1, file2, null);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void convertFileCollectionToFileArrayTest() throws IOException {
		File[] fileArray = new File[2];

		try {
			File file1 = folder.newFile("file1.txt");
			File file2 = folder.newFile("file2.txt");
			fileArray[0] = file1;
			fileArray[1] = file2;
			List<File> listFile = new ArrayList<File>();
			listFile.add(file1);
			listFile.add(file2);

			assertTrue(Arrays.equals(FileUtils.convertFileCollectionToFileArray(listFile), fileArray));

		} catch (java.io.IOException e) {
			throw new IOException(null, null, e.getCause());
		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void copyDirectoryTest() throws IOException {
		try {
			File dir1 = folder.newFolder("dir1");
			File dir2 = folder.newFolder("dir2");
			FileUtils.copyDirectory(dir1, dir2);
		} catch (java.io.IOException e) {
			throw new IOException(null, null, e.getCause());
		}
	}

	@Test(expected = NullPointerException.class)
	public void copyDirectoryNullPTest() throws IOException {
		try {
			File dir1 = folder.newFolder("dir1");
			File dir2 = folder.newFolder("dir2");
			dir1 = null;
			FileUtils.copyDirectory(dir1, dir2);
		} catch (java.io.IOException e) {
			throw new IOException(null, null, e.getCause());
		}
	}

	@Test(expected = IOException.class)
	public void copyDirectoryIOTest() throws IOException {

		File dir1 = new File("");
		File dir2 = new File("");
		FileUtils.copyDirectory(dir1, dir2);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void copyFileTest() throws IOException {
		try {
			File file1 = folder.newFile("file1.txt");
			File file2 = folder.newFile("file2.txt");
			FileUtils.copyFile(file1, file2);

		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = NullPointerException.class)
	public void copyFileNullPTest() throws IOException {
		try {
			File file1 = folder.newFolder("file1");
			File file2 = folder.newFolder("file2");
			file1 = null;
			FileUtils.copyFile(file1, file2);
		} catch (java.io.IOException e) {
			throw new IOException(null, null, e.getCause());
		}
	}

	@Test(expected = IOException.class)
	public void copyFileIOTest() throws IOException {
		File file1 = new File("");
		File file2 = new File("");
		FileUtils.copyFile(file1, file2);
	}

	///////////////////////////////////////////////////////////

	@Test
	public void copyFileStreamTest() throws IOException {
		try {
			File file = folder.newFile("file1.txt");
			OutputStream os = new OutputStream() {

				@Override
				public void write(int b) throws java.io.IOException {
					// do nothing

				}
			};

			FileUtils.copyFile(file, os);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = NullPointerException.class)
	public void copyFileStreamNullPTest() throws IOException {
		try {
			File file = folder.newFile("file.txt");
			OutputStream os = new OutputStream() {

				@Override
				public void write(int b) throws java.io.IOException {
					// do nothing

				}
			};
			file = null;
			FileUtils.copyFile(file, os);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void copyFileStreamIOTest() throws IOException {

		File file = new File("");
		OutputStream os = new OutputStream() {

			@Override
			public void write(int b) throws java.io.IOException {
				// do nothing

			}
		};

		FileUtils.copyFile(file, os);

	}

	///////////////////////////////////////////////////////////

	@Test(expected = IOException.class)
	public void copyInputStreamToFileIOTest() throws IOException {

		InputStream istream = new InputStream() {

			@Override
			public int read() throws java.io.IOException {
				return 0;
			}
		};
		File file1 = new File("");
		FileUtils.copyInputStreamToFile(istream, file1);

	}

	///////////////////////////////////////////////////////////

	@Test(expected = IOException.class)
	public void copyToFileIOTest() throws IOException {

		InputStream istream = new InputStream() {

			@Override
			public int read() throws java.io.IOException {
				return 0;
			}
		};
		File file1;
		try {
			file1 = folder.newFolder("dir");
			FileUtils.copyToFile(istream, file1);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void deleteDirectoryTest() throws IOException {
		try {
			File dir = folder.newFolder("dir");
			FileUtils.deleteDirectory(dir);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteDirectoryIllegalArgTest() throws IOException {
		try {
			File dir = folder.newFile("dir");
			FileUtils.deleteDirectory(dir);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void deleteQuietlyTest() throws IOException {
		try {
			File dir = folder.newFile("dir");
			FileUtils.deleteQuietly(dir);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void directoryContainsTest() throws IOException {
		try {
			File dir = folder.newFolder("parentDir");
			File file = dir.getParentFile();
			assertFalse(FileUtils.directoryContains(dir, file));
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void directoryContainsIllegalArgTest() throws IOException {

		File dir;
		try {
			dir = folder.newFile("sampleFile.txt");
			File file = dir.getParentFile();
			assertFalse(FileUtils.directoryContains(dir, file));
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void forceDeleteTest() throws IOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			FileUtils.forceDelete(file);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = NullPointerException.class)
	public void forceDeleteNullPTest() throws IOException {

		File file = null;
		FileUtils.forceDelete(file);

	}

	@Test(expected = FileNotFoundException.class)
	public void forceDeleteFileNFTest() throws IOException {

		File file = new File("sampleFile");
		FileUtils.forceDelete(file);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void forceDeleteOnExitTest() throws IOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			FileUtils.forceDeleteOnExit(file);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = NullPointerException.class)
	public void forceDeleteOnExitNullPTest() throws IOException {

		File file = null;
		FileUtils.forceDeleteOnExit(file);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void getFileWithDirTest() {
		try {
			File dir = folder.newFolder("dir");
			String[] names = { "file1.txt", "file2.txt" };
			FileUtils.getFile(dir, names);
		} catch (java.io.IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void getFileTest() {
		String[] names = { "file1.txt", "file2.txt" };
		FileUtils.getFile(names);
	}

	///////////////////////////////////////////////////////////

	@Test
	public void isFileNewerTest() {
		try {
			File file = folder.newFile("sampleFile.txt");
			Date date = new Date(10000);
			assertTrue(FileUtils.isFileNewer(file, date));
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void isFileNewerIllegalArgTest() {

		File file = null;
		Date date = new Date(10000);
		FileUtils.isFileNewer(file, date);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void isFileOlderTest() {
		try {
			File file = folder.newFile("sampleFile.txt");
			Date date = new Date(10000);
			assertFalse(FileUtils.isFileOlder(file, date));
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void isFileOlderIllegalArgTest() {

		File file = null;
		Date date = new Date(10000);
		FileUtils.isFileOlder(file, date);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void isSymlinkTest() throws IOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			assertFalse(FileUtils.isSymlink(file));
		} catch (java.io.IOException e) {

		}
	}


	///////////////////////////////////////////////////////////

	@Test
	public void iterateFilesTest() {
		try {
			File directory = folder.newFolder("sampleFolder");
			Iterator<File> fileIt = FileUtils.iterateFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			fileIt.hasNext();
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void lineIteratorTest() throws IOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			LineIterator lItr = FileUtils.lineIterator(file);
			lItr.hasNext();
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void lineIteratorIOTest() throws IOException {

		File file = new File("");
		LineIterator lItr = FileUtils.lineIterator(file);
		lItr.hasNext();

	}

	///////////////////////////////////////////////////////////

	@Test
	public void lineIteratorWithEncodingTest() throws IOException {
		try {
			File file = folder.newFile("sampleFile.txt");
			LineIterator lItr = FileUtils.lineIterator(file, "UTF-8");
			lItr.hasNext();
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void lineIteratorWithEncodingIOTest() throws IOException {

		File file = new File("");
		LineIterator lItr = FileUtils.lineIterator(file, "UTF-8");
		lItr.hasNext();

	}

	///////////////////////////////////////////////////////////

	@Test
	public void listFilesTest() {

		File directory;
		try {
			directory = folder.newFolder("directory");
			FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void listFilesAndDirsTest() {

		File directory;
		try {
			directory = folder.newFolder("directory");
			FileUtils.listFilesAndDirs(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test(expected = FileExistsException.class)
	public void moveDirectoryFileExistsTest() throws IOException {
		try {
			File dir1 = folder.newFolder("dir1");
			File dir2 = folder.newFolder("dir2");
			FileUtils.moveDirectory(dir1, dir2);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void moveDirectoryIOTest() throws IOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = new File("");
			FileUtils.moveDirectory(dirSource, dirDest);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = NullPointerException.class)
	public void moveDirectoryNullPTest() throws IOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = null;
			FileUtils.moveDirectory(dirSource, dirDest);
		} catch (java.io.IOException e) {

		}
	}

	@Test
	public void moveDirectoryTest() throws IOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = new File("moveDirSampleFolder");
			FileUtils.moveDirectory(dirSource, dirDest);
			FileUtils.deleteQuietly(dirDest);
		} catch (java.io.IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test(expected = IOException.class)
	public void moveDirectoryToDirectoryIOTest() throws IOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = new File("sampleFolder");
			FileUtils.moveDirectoryToDirectory(dirSource, dirDest, false);

		} catch (java.io.IOException e) {

		}
	}

	@Test
	public void moveDirectoryToDirectoryTest() throws IOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = folder.newFolder("sampleFolder");
			FileUtils.moveDirectoryToDirectory(dirSource, dirDest, false);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = NullPointerException.class)
	public void moveDirectoryToDirectoryNullPTest() throws IOException {
		try {
			File dirSource = folder.newFolder("dirSource");
			File dirDest = null;
			FileUtils.moveDirectoryToDirectory(dirSource, dirDest, false);
		} catch (java.io.IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test(expected = FileExistsException.class)
	public void moveFileFileExistsTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile");
			File destFile = folder.newFile("destFile");
			FileUtils.moveFile(srcFile, destFile);
		} catch (java.io.IOException e) {

		}
	}

	@Test
	public void moveFileTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destFile = new File("moveFileDestFile.txt");
			FileUtils.moveFile(srcFile, destFile);
			FileUtils.deleteQuietly(destFile);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = NullPointerException.class)
	public void moveFileNullPTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destFile = null;
			FileUtils.moveFile(srcFile, destFile);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void moveFileIOTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile");
			File destFile = new File("");
			FileUtils.moveFile(srcFile, destFile);
		} catch (java.io.IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void moveFileToDirectoryTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = folder.newFolder("moveFileToDirectoryDestDir");
			FileUtils.moveFileToDirectory(srcFile, destDir, false);
			FileUtils.deleteQuietly(destDir);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = NullPointerException.class)
	public void moveFileToDirectoryNullPTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = null;
			FileUtils.moveFileToDirectory(srcFile, destDir, false);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void moveFileToDirectoryIOTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile");
			File destDir = new File("");
			FileUtils.moveFileToDirectory(srcFile, destDir, false);
		} catch (java.io.IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void moveToDirectoryTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = folder.newFolder("moveToDirectoryDestDir");
			FileUtils.moveToDirectory(srcFile, destDir, false);
			FileUtils.deleteQuietly(destDir);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void moveToDirectoryIOTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = new File("");
			FileUtils.moveToDirectory(srcFile, destDir, false);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = NullPointerException.class)
	public void moveToDirectoryNullPTest() throws IOException {
		try {
			File srcFile = folder.newFile("srcFile.txt");
			File destDir = null;
			FileUtils.moveToDirectory(srcFile, destDir, false);

		} catch (java.io.IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void openInputStreamTest() throws IOException {

		try {

			File file = folder.newFile();
			FileUtils.openInputStream(file);

		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = FileNotFoundException.class)
	public void openInputStreamFileNFTest() throws IOException {

		File file = new File("openIPStramfile.txt");
		FileUtils.openInputStream(file);

	}

	@Test(expected = IOException.class)
	public void openInputStreamIOTest() throws IOException {

		File file;
		try {
			file = folder.newFolder();
			FileUtils.openInputStream(file);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void openOutputStreamTest() throws IOException {

		try {
			File file = folder.newFile();
			FileUtils.openOutputStream(file);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void openOutputStreamIOTest() throws IOException {

		File file;
		try {
			file = folder.newFolder();
			FileUtils.openOutputStream(file);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void openOutputStreamWithAppendTest() throws IOException {

		try {
			File file = folder.newFile();
			FileUtils.openOutputStream(file, true);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void openOutputStreamWithAppendIOTest() throws IOException {

		File file;
		try {
			file = folder.newFolder();
			FileUtils.openOutputStream(file, true);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void readFileToByteArrayTest() throws IOException {

		try {
			File file = folder.newFile();
			FileUtils.readFileToByteArray(file);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void readFileToByteArrayIOTest() throws IOException {
		File file = new File("");
		FileUtils.readFileToByteArray(file);
	}

	///////////////////////////////////////////////////////////

	@Test
	public void readFileToStringTest() throws IOException {

		try {
			File file = folder.newFile();
			FileUtils.readFileToString(file, null);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void readFileToStringIOTest() throws IOException {
		File file = new File("");
		FileUtils.readFileToString(file, null);
	}

	///////////////////////////////////////////////////////////

	@Test
	public void readLinesTest() throws IOException {
		try {
			File file = folder.newFile();
			FileUtils.readLines(file, Charset.forName("UTF-8"));
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void readLinesIOTest() throws IOException {

		try {
			File file = folder.newFolder();
			FileUtils.readLines(file, Charset.forName("UTF-8"));
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void readLinesWithStringEncodingTest() throws IOException {
		try {
			File file = folder.newFile();
			FileUtils.readLines(file, "UTF-8");
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = UnsupportedCharsetException.class)
	public void readLinesWithStringEncodingUCharsetTest() throws IOException {
		try {
			File file = folder.newFile();
			FileUtils.readLines(file, "UT");
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void readLinesWithStringEncodingIOTest() throws IOException {
		try {
			File file = folder.newFolder();
			FileUtils.readLines(file, "UTF-8");
		} catch (java.io.IOException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test
	public void sizeOfTest() {
		try {
			File file = folder.newFile();
			assertEquals(0, FileUtils.sizeOf(file));
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = NullPointerException.class)
	public void sizeOfNullPTest() {

		File file = null;
		assertEquals(0, FileUtils.sizeOf(file));

	}

	@Test(expected = IllegalArgumentException.class)
	public void sizeOfIllegalArgTest() {

		File file = new File("");
		assertEquals(0, FileUtils.sizeOf(file));

	}

	///////////////////////////////////////////////////////////

	@Test
	public void sizeOfDirectoryTest() {
		try {
			File dir = folder.newFolder();
			assertEquals(0, FileUtils.sizeOfDirectory(dir));
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = NullPointerException.class)
	public void sizeOfDirectoryNullPTest() {

		File dir = null;
		assertEquals(0, FileUtils.sizeOfDirectory(dir));

	}

	///////////////////////////////////////////////////////////

	@Test
	public void toFileTest() {
		try {
			FileUtils.toFile(new URL("http://www.example.com/docs/resource1.html"));
		} catch (MalformedURLException e) {

		}
	}

	///////////////////////////////////////////////////////////

	@Test(expected = IllegalArgumentException.class)
	public void toFilesTest() {

		try {
			URL[] urls = new URL[2];
			URL url1 = new URL("http://www.example.com/docs/resource1.ht");
			URL url2 = new URL("http://www.example.com/docs/resource1.ht");
			urls[0] = url1;
			urls[1] = url2;
			File[] file = FileUtils.toFiles(urls);
			assertThat(file, null);
		} catch (MalformedURLException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void toURLsTest() throws IOException {
		try {
			File file1 = folder.newFile();
			File file2 = folder.newFile();
			File[] fileArray = { file1, file2 };
			FileUtils.toURLs(fileArray);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = NullPointerException.class)
	public void toURLsNullPTest() throws IOException {
		try {
			File file1 = folder.newFile();
			File file2 = null;
			File[] fileArray = { file1, file2 };
			FileUtils.toURLs(fileArray);
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test(expected = NullPointerException.class)
	public void waitForNullPTest() {

		File file = null;
		assertFalse(FileUtils.waitFor(file, 2));

	}

	@Test
	public void waitForTest() {

		File file;
		try {
			file = folder.newFile("waitForFile.txt");
			assertTrue(FileUtils.waitFor(file, 2));
		} catch (java.io.IOException e) {

		}

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeWithStringEncTest() throws IOException {
		try {
			File file = folder.newFile();
			FileUtils.write(file, "", "UTF-8");
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeWithStringEncIOTest() throws IOException {

		File file = new File("");
		FileUtils.write(file, "", "UTF-8");

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeWithCharsetEncTest() throws IOException {
		try {
			File file = folder.newFile();
			FileUtils.write(file, "", Charset.defaultCharset());
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeWithCharsetEncIOTest() throws IOException {

		File file = new File("");
		FileUtils.write(file, "", Charset.defaultCharset());

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeWithCharsetEncAndAppendTest() throws IOException {
		try {
			File file = folder.newFile();
			FileUtils.write(file, "", Charset.defaultCharset(), false);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeWithCharsetEncAndAppendIOTest() throws IOException {

		File file = new File("");
		FileUtils.write(file, "", Charset.defaultCharset(), false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeWithStringEncAndAppendTest() throws IOException {
		try {
			File file = folder.newFile();
			FileUtils.write(file, "", "UTF-8", false);
		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeWithStringEncAndAppendIOTest() throws IOException {

		File file = new File("");
		FileUtils.write(file, "", "UTF-8", false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeByteArrayToFileTest() throws IOException {
		try {
			File file = folder.newFile();
			byte[] data = new byte[2];
			FileUtils.writeByteArrayToFile(file, data);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void writeByteArrayToFileIOTest() throws IOException {

		File file = new File("");
		byte[] data = new byte[2];
		FileUtils.writeByteArrayToFile(file, data);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeByteArrayToFileWithAppendTest() throws IOException {
		try {
			File file = folder.newFile();
			byte[] data = new byte[2];
			FileUtils.writeByteArrayToFile(file, data, false);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void writeByteArrayToFileWithAppendIOTest() throws IOException {

		File file = new File("");
		byte[] data = new byte[2];
		FileUtils.writeByteArrayToFile(file, data, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeByteArrayToFileWithOffsetTest() throws IOException {
		try {
			File file = folder.newFile();
			byte[] data = new byte[2];
			FileUtils.writeByteArrayToFile(file, data, 0, 1);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void writeByteArrayToFileWithOffsetIOTest() throws IOException {

		File file = new File("");
		byte[] data = new byte[2];
		FileUtils.writeByteArrayToFile(file, data, 0, 1);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeByteArrayToFileWithOffsetAndAppendTest() throws IOException {
		try {
			File file = folder.newFile();
			byte[] data = new byte[2];
			FileUtils.writeByteArrayToFile(file, data, 0, 1, false);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void writeByteArrayToFileWithOffsetAndAppendIOTest() throws IOException {

		File file = new File("");
		byte[] data = new byte[2];
		FileUtils.writeByteArrayToFile(file, data, 0, 1, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesTest() throws IOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtils.writeLines(file, lines);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeLinesIOTest() throws IOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtils.writeLines(file, lines);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithAppendTest() throws IOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtils.writeLines(file, lines, false);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeLinesWithAppendIOTest() throws IOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtils.writeLines(file, lines, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithAppendAndLineEndingAndEncTest() throws IOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtils.writeLines(file, "UTF-8", lines, null, false);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeLinesWithAppendAndLineEndingAndEncIOTest() throws IOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtils.writeLines(file, "UTF-8", lines, null, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithLineEndingTest() throws IOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtils.writeLines(file, lines, null);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeLinesWithLineEndingIOTest() throws IOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtils.writeLines(file, lines, null);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithLineEndingAndEncTest() throws IOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtils.writeLines(file, "UTF-8", lines, null);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeLinesWithLineEndingAndEncIOTest() throws IOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtils.writeLines(file, "UTF-8", lines, null);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithEncTest() throws IOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtils.writeLines(file, "UTF-8", lines);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeLinesWithEncIOTest() throws IOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtils.writeLines(file, "UTF-8", lines);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithEncAndAppendTest() throws IOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtils.writeLines(file, "UTF-8", lines, false);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeLinesWithEncAndAppendIOTest() throws IOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtils.writeLines(file, "UTF-8", lines, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeLinesWithLineEndingAndAppendTest() throws IOException {
		try {
			File file = folder.newFile();
			Collection<String> lines = new ArrayList<String>();
			FileUtils.writeLines(file, lines, null, false);

		} catch (java.io.IOException e) {

		}
	}

	@Test(expected = IOException.class)
	public void writeLinesWithLineEndingAndAppendIOTest() throws IOException {

		File file = new File("");
		Collection<String> lines = new ArrayList<String>();
		FileUtils.writeLines(file, lines, null, false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeStringToFileTest() throws IOException {
		try {
			File file = folder.newFile();
			String data = "sampleData";
			FileUtils.writeStringToFile(file, data, Charset.defaultCharset());
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void writeStringToFileIOTest() throws IOException {

		File file = new File("");
		String data = "sampleData";
		FileUtils.writeStringToFile(file, data, Charset.defaultCharset());

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeStringToFileWithStringEncAndAppendTest() throws IOException {
		try {
			File file = folder.newFile();
			String data = "sampleData";
			FileUtils.writeStringToFile(file, data, "UTF-8", false);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void writeStringToFileWithStringEncAndAppendIOTest() throws IOException {

		File file = new File("");
		String data = "sampleData";
		FileUtils.writeStringToFile(file, data, "UTF-8", false);

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeStringToFileWithStringEncTest() throws IOException {
		try {
			File file = folder.newFile();
			String data = "sampleData";
			FileUtils.writeStringToFile(file, data, "UTF-8");
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void writeStringToFileWithStringEncIOTest() throws IOException {

		File file = new File("");
		String data = "sampleData";
		FileUtils.writeStringToFile(file, data, "UTF-8");

	}

	///////////////////////////////////////////////////////////

	@Test
	public void writeStringToFileWithAppendTest() throws IOException {
		try {
			File file = folder.newFile();
			String data = "sampleData";
			FileUtils.writeStringToFile(file, data, Charset.defaultCharset(), false);
		} catch (java.io.IOException e) {

		}

	}

	@Test(expected = IOException.class)
	public void writeStringToFileWithAppendIOTest() throws IOException {

		File file = new File("");
		String data = "sampleData";
		FileUtils.writeStringToFile(file, data, Charset.defaultCharset(), false);

	}

}
