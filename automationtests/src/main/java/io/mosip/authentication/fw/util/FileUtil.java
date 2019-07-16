package io.mosip.authentication.fw.util;

import java.io.BufferedReader; 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import io.mosip.authentication.fw.precon.JsonPrecondtion;

/**
 * Class is to perform all file util such as create,read files
 * 
 * @author Vignesh
 *
 */
public class FileUtil{
	
	private static final Logger FILEUTILITY_LOGGER = Logger.getLogger(FileUtil.class);

	/**
	 * The method will get list of files in a folder
	 * 
	 * @param folder, Folder path
	 * @return list of files
	 */ 
	public static List<File> getFolders(File folder) {
		List<File> list = new ArrayList<File>();
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory())
				list.add(listOfFolders[j]);
		}
		return list;
	}
	
	/**
	 * The method will get file path from folder using file name keywords
	 * 
	 * @param folder, Folder path
	 * @param keywordToFind, file keyword to find
	 * @return File
	 */
	public static File getFilePath(File folder, String keywordToFind) {
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].getName().contains(keywordToFind))
				return listOfFolders[j].getAbsoluteFile();
		}
		return null;
	}
	
	/**
	 * The method verify file present in list of files
	 * 
	 * @param listOfFiles
	 * @param keywordToFind
	 * @return True or False
	 */
	public static boolean verifyFilePresent(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind))
				return true;
		}
		return false;
	}
	
	/**
	 * The method will create and write file
	 * 
	 * @param fileName, file name to used
	 * @param content, content to be written in file
	 * @return True or false
	 */
	public static boolean createAndWriteFile(String fileName, String content) {
		try {
			Path path = Paths.get(AuthTestsUtil.getTestFolder().getAbsolutePath() + "/" + fileName);
			Charset charset = Charset.forName("UTF-8");
			BufferedWriter writer = Files.newBufferedWriter(path, charset);
			writer.write(content);
			writer.flush();
			writer.close();
			return true;
		} catch (Exception e) {
			FILEUTILITY_LOGGER.error("Exception " + e);
			return false;
		}
	}
	
	/**
	 * The method will write file
	 * 
	 * @param filePath, file path to use used
	 * @param content, Content to written in file
	 * @return true or false
	 */
	public static boolean writeFile(String filePath, String content) {
		try {
			Path path = Paths.get(filePath);
			BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			writer.write(content);
			writer.flush();
			writer.close();
			return true;
		} catch (Exception e) {
			FILEUTILITY_LOGGER.error("Exception " + e);
			return false;
		}
	}
	
	/**
	 * The method will create and write file for IDREPO
	 * 
	 * @param fileName, file name to be used
	 * @param content, content to be used
	 * @return true or false
	 */
	public static boolean createAndWriteFileForIdRepo(String fileName, String content) {
		try {
			Path path = Paths
					.get(new File(RunConfigUtil.getResourcePath() + RunConfigUtil.objRunConfig.getStoreUINDataPath() + "/" + fileName)
							.getAbsolutePath());
			if (!path.toFile().exists()) {
				Charset charset = Charset.forName("UTF-8");
				createFile(path.toFile(), "");
				BufferedWriter writer = Files.newBufferedWriter(path, charset);
				writer.write(JsonPrecondtion.convertJsonContentToXml(content));
				writer.flush();
				writer.close();
			}
			return true;
		} catch (Exception e) {
			FILEUTILITY_LOGGER.error("Exception " + e);
			return false;
		}
	}	
	
	/**
	 * The method will check file exist for idrepo
	 * 
	 * @param fileName, file name to check
	 * @return true or false
	 */
	public static boolean checkFileExistForIdRepo(String fileName) {
		Path path = Paths.get(new File(RunConfigUtil.getResourcePath() + RunConfigUtil.objRunConfig.getStoreUINDataPath()
				+ "/" + fileName).getAbsolutePath());
		return path.toFile().exists();
	}

	/**
	 * The method will write output using file outputstream
	 * 
	 * @param filePath
	 * @param content
	 */
	public void writeOutput(String filePath, String content) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			Writer out = new OutputStreamWriter(fos, "UTF8");
			out.write(content);
			out.close();
		} catch (IOException e) {
			FILEUTILITY_LOGGER.error("Exception : " + e.getMessage());
		}
	}

	/**
	 * The method will read input from file path
	 * 
	 * @param filePath
	 * @return String, content from file
	 */
	public static String readInput(String filePath) {
		StringBuffer buffer = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			Reader in = new BufferedReader(isr);
			int ch;
			while ((ch = in.read()) > -1) {
				buffer.append((char) ch);
			}
			in.close();
			return buffer.toString();
		} catch (IOException e) {
			FILEUTILITY_LOGGER.error("Exception : " + e.getMessage());
			return null;
		}
	}
	  
	/**
	 * The method will create file either with dummy data or some data
	 * 
	 * @param fileName
	 * @param content
	 * @return true or false
	 */
	public static boolean createFile(File fileName, String content) {
		try {
			makeDir(fileName.getParentFile().toString());
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			FILEUTILITY_LOGGER.error("Exception " + e);
			return false;
		}
	}
	
	/**
	 * The method will male new directory
	 * 
	 * @param path
	 * @return true or false
	 */
	public static boolean makeDir(String path) {
		File file = new File(path);
		return file.mkdirs();
	}
	
	/**
	 * The method will get file from list of files
	 * 
	 * @param listOfFiles
	 * @param keywordToFind
	 * @return File
	 */ 
	public static File getFileFromList(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return listOfFiles[j];
			}
		}
		return null;
	}
	
	public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
	}

}
