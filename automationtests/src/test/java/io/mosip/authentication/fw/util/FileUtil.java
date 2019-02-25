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

/**
 * Class is to perform all file util such as create,read files
 * 
 * @author Vignesh
 *
 */
public class FileUtil extends IdaScriptsUtil {
	
	private static Logger logger = Logger.getLogger(FileUtil.class);
	public List<File> getFolders(File folder) {
		List<File> list = new ArrayList<File>();
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory())
				list.add(listOfFolders[j]);
		}
		return list;
	}
	
	public File getFilePath(File folder, String keywordToFind) {
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].getName().contains(keywordToFind))
				return listOfFolders[j].getAbsoluteFile();
		}
		return null;
	}
	
	public boolean verifyFilePresent(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind))
				return true;
		}
		return false;
	}
	
	public boolean createAndWriteFile(String fileName, String content) {
		try {
			Path path = Paths.get(getTestFolder().getAbsolutePath() + "\\"+fileName);
			Charset charset = Charset.forName("UTF-8");
			BufferedWriter writer = Files.newBufferedWriter(path,charset);
			writer.write(content);
            writer.flush();
            writer.close();           
			return true;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return false;
		}
	}
	
	public boolean writeFile(String filePath, String content) {
		try {
			Path path = Paths.get(filePath);
			Charset charset = Charset.forName("UTF-8");
			BufferedWriter writer = Files.newBufferedWriter(path,StandardCharsets.UTF_8);
			writer.write(content);
            writer.flush();
            writer.close();           
			return true;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return false;
		}
	}
	
	public boolean createAndWriteFileForIdRepo(String fileName, String content) {
		try {
			Path path = Paths.get(RunConfig.getUserDirectory()+RunConfig.getSrcPath()+RunConfig.getStoreUINDataPath()+ "\\"+fileName);
			if(!path.toFile().exists()) { 
			Charset charset = Charset.forName("UTF-8");
			createFile(path.toFile(),"");
			BufferedWriter writer = Files.newBufferedWriter(path, charset);
			writer.write(content);
            writer.flush();
            writer.close();
			}
			return true;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return false;
		}
	}
	
	public boolean checkFileExistForIdRepo(String fileName) {
		Path path = Paths.get(RunConfig.getUserDirectory() + RunConfig.getSrcPath() + RunConfig.getStoreUINDataPath()
				+ "\\" + fileName);
		return path.toFile().exists();
	}

	public void writeOutput(String filePath, String content) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			Writer out = new OutputStreamWriter(fos, "UTF8");
			out.write(content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String readInput(String filePath) {
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
			e.printStackTrace();
			return null;
		}
	}
	  
	public boolean createFile(File fileName,String content) {
		try {
			makeDir(fileName.getParentFile().toString());
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return false;
		}
	}
	
	public boolean makeDir(String path) {
		File file = new File(path);
		return file.mkdirs();
	}

}
