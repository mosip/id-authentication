package io.mosip.kernel.core.test.util;

import static org.junit.Assert.assertTrue;

import java.io.File;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.NullPointerException;
import io.mosip.kernel.core.util.ZipUtils;

/**
 * Test class for ZipUtil class
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public class ZipUtilTest {

	public static ZipUtils zip;
	
	@Test
	public void zipByteArray() throws FileNotFoundException, IOException, java.io.IOException, URISyntaxException {
 
    	    byte[] data = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("SampleFile.txt").toURI())); 
    	    byte[] returnedzippedByteArray = ZipUtils.zipByteArray(data);
    	    String outputFile = System.getProperty("user.dir") + "\\compressedByteArray.zip";   
    	    Files.write(Paths.get(outputFile), returnedzippedByteArray);
    	    File returnFile= new File(outputFile);
        
    	    assertTrue(returnFile.exists());	
    	    File file = new File(outputFile);
    		file.delete();
	}
	
	
	@Test
	public void unzipByteArray() throws FileNotFoundException, IOException, java.io.IOException, URISyntaxException {
 
    	    byte[] data = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("SampleFile.txt").toURI())); 
    	    byte[] returnedzippedByteArray = ZipUtils.zipByteArray(data);
    	    String finalPath = System.getProperty("user.dir") + "\\src\\final.txt";
    	      
    	    byte[] returnedunzipByteArray =ZipUtils.unzipByteArray(returnedzippedByteArray);
    	    Files.write(Paths.get(finalPath), returnedunzipByteArray);
    	    File returnFile= new File(finalPath);
        
    	    assertTrue(returnFile.exists());	
    	    File file = new File(finalPath);
    		file.delete();
	}


	@Test
	public void zipFileTest() throws FileNotFoundException, IOException, java.io.IOException {

		String outputFile = System.getProperty("user.dir") + "\\compressed.zip";
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("SampleFile.txt").getFile();
		boolean ren = ZipUtils.zipFile(inputFile, outputFile);
		assertTrue(ren);

		File file = new File(outputFile);
		file.delete();
	}

	@Test(expected = FileNotFoundException.class)
	public void zipFileFileNotFoundExceptionTestWithInputFile() throws FileNotFoundException, IOException {
		String inputFile = null;
		String outputFile = "";
		ZipUtils.zipFile(inputFile, outputFile);
	}

	@Test
	public void zipMultipleFileTest() throws FileNotFoundException, IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		String inputMultFile1 = classLoader.getResource("File1.txt").getFile();
		String inputMultFile2 = classLoader.getResource("File2.txt").getFile();
		String outputMulFile = System.getProperty("user.dir") + "\\compressedMulty.zip";
		String[] inputMultFile = { inputMultFile1, inputMultFile2 };

		boolean ren = ZipUtils.zipMultipleFile(inputMultFile, outputMulFile);
		assertTrue(ren);

		File file = new File(outputMulFile);
		file.delete();
	}

	@Test(expected = FileNotFoundException.class)
	public void zipMultFileNotFoundExceptionTest() throws FileNotFoundException, IOException {
		String[] inputMultFile = { "" };
		String outputMulFile = "";

		ZipUtils.zipMultipleFile(inputMultFile, outputMulFile);
	}

	@Test
	public void zipDirectoryTest() throws FileNotFoundException, IOException {

		String outputDir = System.getProperty("user.dir") + "\\compressedDir.zip";
		ClassLoader classLoader = getClass().getClassLoader();
		String inputDir = classLoader.getResource("test").getFile();

		boolean ren = ZipUtils.zipDirectory(inputDir, outputDir);
		assertTrue(ren);

		File file = new File(outputDir);
		file.delete();
	}

	@Test(expected = FileNotFoundException.class)
	public void zipDirFileNotFoundExceptionTest() throws FileNotFoundException, IOException {
		String inputDir = "";
		String outputDir = "";

		ZipUtils.zipDirectory(inputDir, outputDir);
	}

	@Test
	public void unZipFileTest() throws FileNotFoundException, IOException {

		String outputUnZip = System.getProperty("user.dir") + "\\unzip";
		ClassLoader classLoader = getClass().getClassLoader();
		String inputZipFile = classLoader.getResource("File1.zip").getFile();

		boolean ren = ZipUtils.unZipFile(inputZipFile, outputUnZip);
		assertTrue(ren);

		File file = new File(outputUnZip + "File1.txt");
		file.delete();
	}

	@Test(expected = FileNotFoundException.class)
	public void unZipFileNotFoundExceptionTest() throws FileNotFoundException, IOException {

		String inputZipFile = "";
		String outputUnZip = "";
		ZipUtils.unZipFile(inputZipFile, outputUnZip);
	}

	@Test(expected = NullPointerException.class)
	public void unZipNullPointerException() throws FileNotFoundException, IOException {

		String inputZipFile = null;
		String outputUnZip = null;
		ZipUtils.unZipFile(inputZipFile, outputUnZip);
	}

	@Test
	public void unzipDTest() throws FileNotFoundException, IOException, java.io.IOException {

		String outputUnZip = System.getProperty("user.dir") + "\\unzip";
		ClassLoader classLoader = getClass().getClassLoader();
		String zipFile = classLoader.getResource("test.zip").getFile();

		boolean ren = ZipUtils.unZipDirectory(zipFile, outputUnZip);
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

	@Test(expected = FileNotFoundException.class)
	public void unZipDirNotFoundExceptionTest() throws FileNotFoundException, IOException {
		String outputFile = "";
		String outputUnZip = "";
		ZipUtils.unZipDirectory(outputFile, outputUnZip);
	}

}
