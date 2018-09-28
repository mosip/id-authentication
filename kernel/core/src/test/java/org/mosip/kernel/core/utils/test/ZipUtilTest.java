package org.mosip.kernel.core.utils.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.mosip.kernel.core.utils.ZipUtil;
import org.mosip.kernel.core.utils.exception.MosipFileNotFoundException;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.exception.MosipNullPointerException;

/**
 * Test class for ZipUtil class
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public class ZipUtilTest {

	public static ZipUtil zip;
	
	@Test
	public void zipByteArray() throws MosipFileNotFoundException, MosipIOException, IOException, URISyntaxException {
 
    	    byte[] data = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("SampleFile.txt").toURI())); 
    	    byte[] returnedzippedByteArray = ZipUtil.zipByteArray(data);
    	    String outputFile = System.getProperty("user.dir") + "\\compressedByteArray.zip";   
    	    Files.write(Paths.get(outputFile), returnedzippedByteArray);
    	    File returnFile= new File(outputFile);
        
    	    assertTrue(returnFile.exists());	
    	    File file = new File(outputFile);
    		file.delete();
	}
	
	
	@Test
	public void unzipByteArray() throws MosipFileNotFoundException, MosipIOException, IOException, URISyntaxException {
 
    	    byte[] data = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("SampleFile.txt").toURI())); 
    	    byte[] returnedzippedByteArray = ZipUtil.zipByteArray(data);
    	    String finalPath = System.getProperty("user.dir") + "\\src\\final.txt";
    	      
    	    byte[] returnedunzipByteArray =ZipUtil.unzipByteArray(returnedzippedByteArray);
    	    Files.write(Paths.get(finalPath), returnedunzipByteArray);
    	    File returnFile= new File(finalPath);
        
    	    assertTrue(returnFile.exists());	
    	    File file = new File(finalPath);
    		file.delete();
	}


	@Test
	public void zipFileTest() throws MosipFileNotFoundException, MosipIOException, IOException {

		String outputFile = System.getProperty("user.dir") + "\\compressed.zip";
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("SampleFile.txt").getFile();
		boolean ren = ZipUtil.zipFile(inputFile, outputFile);
		assertTrue(ren);

		File file = new File(outputFile);
		file.delete();
	}

	@Test(expected = MosipFileNotFoundException.class)
	public void zipFileFileNotFoundExceptionTestWithInputFile() throws MosipFileNotFoundException, MosipIOException {
		String inputFile = null;
		String outputFile = "";
		ZipUtil.zipFile(inputFile, outputFile);
	}

	@Test
	public void zipMultipleFileTest() throws MosipFileNotFoundException, MosipIOException {

		ClassLoader classLoader = getClass().getClassLoader();
		String inputMultFile1 = classLoader.getResource("File1.txt").getFile();
		String inputMultFile2 = classLoader.getResource("File2.txt").getFile();
		String outputMulFile = System.getProperty("user.dir") + "\\compressedMulty.zip";
		String[] inputMultFile = { inputMultFile1, inputMultFile2 };

		boolean ren = ZipUtil.zipMultipleFile(inputMultFile, outputMulFile);
		assertTrue(ren);

		File file = new File(outputMulFile);
		file.delete();
	}

	@Test(expected = MosipFileNotFoundException.class)
	public void zipMultFileNotFoundExceptionTest() throws MosipFileNotFoundException, MosipIOException {
		String[] inputMultFile = { "" };
		String outputMulFile = "";

		ZipUtil.zipMultipleFile(inputMultFile, outputMulFile);
	}

	@Test
	public void zipDirectoryTest() throws MosipFileNotFoundException, MosipIOException {

		String outputDir = System.getProperty("user.dir") + "\\compressedDir.zip";
		ClassLoader classLoader = getClass().getClassLoader();
		String inputDir = classLoader.getResource("test").getFile();

		boolean ren = ZipUtil.zipDirectory(inputDir, outputDir);
		assertTrue(ren);

		File file = new File(outputDir);
		file.delete();
	}

	@Test(expected = MosipFileNotFoundException.class)
	public void zipDirFileNotFoundExceptionTest() throws MosipFileNotFoundException, MosipIOException {
		String inputDir = "";
		String outputDir = "";

		ZipUtil.zipDirectory(inputDir, outputDir);
	}

	@Test
	public void unZipFileTest() throws MosipFileNotFoundException, MosipIOException {

		String outputUnZip = System.getProperty("user.dir") + "\\unzip";
		ClassLoader classLoader = getClass().getClassLoader();
		String inputZipFile = classLoader.getResource("File1.zip").getFile();

		boolean ren = ZipUtil.unZipFile(inputZipFile, outputUnZip);
		assertTrue(ren);

		File file = new File(outputUnZip + "File1.txt");
		file.delete();
	}

	@Test(expected = MosipFileNotFoundException.class)
	public void unZipFileNotFoundExceptionTest() throws MosipFileNotFoundException, MosipIOException {

		String inputZipFile = "";
		String outputUnZip = "";
		ZipUtil.unZipFile(inputZipFile, outputUnZip);
	}

	@Test(expected = MosipNullPointerException.class)
	public void unZipNullPointerException() throws MosipFileNotFoundException, MosipIOException {

		String inputZipFile = null;
		String outputUnZip = null;
		ZipUtil.unZipFile(inputZipFile, outputUnZip);
	}

	@Test
	public void unzipDTest() throws MosipFileNotFoundException, MosipIOException, IOException {

		String outputUnZip = System.getProperty("user.dir") + "\\unzip";
		ClassLoader classLoader = getClass().getClassLoader();
		String zipFile = classLoader.getResource("test.zip").getFile();

		boolean ren = ZipUtil.unZipDirectory(zipFile, outputUnZip);
		assertTrue(ren);

		File dir = new File(outputUnZip);
		String[] entries = dir.list();
		for (String s : entries) {
			File currentFile = new File(dir.getPath(), s);
			if (currentFile.isDirectory()) {
				String[] innerEntries = currentFile.list();
				for (String s1 : innerEntries) {
					File innerCurrentFile = new File(currentFile.getPath(), s1);
					innerCurrentFile.delete();
				}
			}
			currentFile.delete();
		}
		dir.delete();
	}

	@Test(expected = MosipFileNotFoundException.class)
	public void unZipDirNotFoundExceptionTest() throws MosipFileNotFoundException, MosipIOException {
		String outputFile = "";
		String outputUnZip = "";
		ZipUtil.unZipDirectory(outputFile, outputUnZip);
	}

}
