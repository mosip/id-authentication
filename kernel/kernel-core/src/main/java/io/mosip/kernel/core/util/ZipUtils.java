package io.mosip.kernel.core.util;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import io.mosip.kernel.core.exception.DataFormatException;
import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.NullPointerException;
import io.mosip.kernel.core.util.constant.ZipUtilConstants;

/**
 * Utilities for Zip and UnZip operations.
 * 
 * Provide Zip utility for usage across the application to Zip and unZip Files
 * and Directory
 * 
 * Size of the files and Folders according to business needs
 * 
 * This ZipUtil will not applicable for RAR or 7Z etc.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
public class ZipUtils {

	/**
	 * Private Constructor for ZipUtil Class
	 */
	private ZipUtils() {

	}

	/**
	 * Method used for zipping a Byte Array
	 * 
	 * @param input
	 *            pass Byte Array want to zip it
	 * 
	 * @return zipped Byte Array
	 * 
	 * @throws IOException
	 *             when file unable to read
	 * 
	 */
	public static byte[] zipByteArray(byte[] input) throws IOException {
		byte[] byReturn = null;
		Deflater oDeflate = new Deflater(Deflater.DEFLATED, false);
		oDeflate.setInput(input);
		oDeflate.finish();
		try (ByteArrayOutputStream oZipStream = new ByteArrayOutputStream()) {

			while (!oDeflate.finished()) {
				byte[] byRead = new byte[1024];
				int iBytesRead = oDeflate.deflate(byRead);
				if (iBytesRead == byRead.length) {
					oZipStream.write(byRead);
				} else {
					oZipStream.write(byRead, 0, iBytesRead);
				}
			}
			oDeflate.end();
			byReturn = oZipStream.toByteArray();
		} catch (java.io.IOException e) {
			throw new IOException(ZipUtilConstants.IO_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}

		return byReturn;
	}

	/**
	 * Method used for unzipping a zipped Byte Array
	 * 
	 * @param input
	 *            pass zipped Byte Array want to unzip it
	 * 
	 * @return returned unzipped Byte Array
	 * 
	 * @throws IOException
	 *             when file unable to read
	 * 
	 * @throws DataFormatException
	 *             Attempting to unzip file that is not zipped
	 * 
	 * @see io.mosip.kernel.core.exception.IOException
	 */
	public static byte[] unzipByteArray(byte[] input) throws IOException {
		byte[] byReturn = null;

		Inflater oInflate = new Inflater(false);
		oInflate.setInput(input);

		try (ByteArrayOutputStream oZipStream = new ByteArrayOutputStream()) {
			while (!oInflate.finished()) {
				byte[] byRead = new byte[1024];
				int iBytesRead = oInflate.inflate(byRead);
				if (iBytesRead == byRead.length) {
					oZipStream.write(byRead);
				} else {
					oZipStream.write(byRead, 0, iBytesRead);
				}
			}
			byReturn = oZipStream.toByteArray();
		} catch (java.util.zip.DataFormatException e) {
			throw new DataFormatException(ZipUtilConstants.DATA_FORMATE_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.DATA_FORMATE_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(ZipUtilConstants.IO_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}

		return byReturn;
	}

	/**
	 * Method used for zipping a single file
	 * 
	 * @param inputFile
	 *            pass single input file address as string, want to Zip it example :
	 *            String inputFile = "D:\\Testfiles\\test.txt";
	 * @param outputFile
	 *            pass Zip file address as string example: String outputZipFile =
	 *            "D:\\Testfiles\\compressed.zip"
	 * 
	 * @return true if zip file is created
	 * 
	 * @throws FileNotFoundException
	 *             when file is not found
	 * @throws IOException
	 *             when file unable to read
	 * 
	 */

	public static boolean zipFile(String inputFile, String outputFile) throws IOException {

		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputFile));
				FileInputStream fis = new FileInputStream(new File(inputFile))) {

			ZipEntry zipEntry = new ZipEntry(new File(inputFile).getName());
			zipOut.putNextEntry(zipEntry);
			readFile(zipOut, fis);

		} catch (java.io.FileNotFoundException e) {
			throw new FileNotFoundException(ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(ZipUtilConstants.IO_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}

		return true;
	}

	/**
	 * This is inner method to read a file
	 * 
	 * @param zipOut
	 *            ZipOutStream object of inputFile
	 * @param fis
	 *            FileInputStream object of inputFile
	 * @throws java.io.IOException
	 *             when file unable to read
	 */
	private static void readFile(ZipOutputStream zipOut, FileInputStream fis) throws java.io.IOException {
		final byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
	}

	/**
	 * Method used for zipping a multiple file
	 * 
	 * @param inputMultFile
	 *            pass list of file names as array of String example : String[]
	 *            inputMultFile = {"D:\\Testfiles\\test.txt",
	 *            "D:\\Testfiles\\test.txt"}
	 * 
	 * @param outputFile
	 *            pass Zip file address as string example: String
	 *            outputMulFile="D:\\Testfiles\\compressedMult.zip"
	 * 
	 * @return true if zip file is created
	 * 
	 * @throws FileNotFoundException
	 *             when file is not found
	 * 
	 * @throws IOException
	 *             when file unable to read
	 */

	public static boolean zipMultipleFile(String[] inputMultFile, String outputFile) throws IOException {

		List<String> srcFiles = new ArrayList<>(Arrays.asList(inputMultFile));
		for (String srcFile : srcFiles) {
			File fileToZip = new File(srcFile);

			try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputFile));
					FileInputStream fis = new FileInputStream(fileToZip)) {

				ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
				zipOut.putNextEntry(zipEntry);
				readFile(zipOut, fis);
			} catch (java.io.FileNotFoundException e) {
				throw new FileNotFoundException(ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
						ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getMessage(), e.getCause());
			} catch (java.io.IOException e) {
				throw new IOException(ZipUtilConstants.IO_ERROR_CODE.getErrorCode(),
						ZipUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
			}
		}
		return true;
	}

	/**
	 * Method used for zipping a directory
	 * 
	 * @param inputDir
	 *            pass Directory name need to be zip example : String inputDir =
	 *            "D:\\Testfiles\\TestDir";
	 * 
	 * @param destDirectory
	 *            pass Zip file address as string example: String outputDir
	 *            ="D:\\Testfiles\\compressedDir.zip";
	 * 
	 * @return true if zip directory is created
	 * 
	 * @throws FileNotFoundException
	 *             when file is not found
	 * 
	 * @throws IOException
	 *             when file unable to read
	 */
	public static boolean zipDirectory(String inputDir, String destDirectory) throws IOException {

		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(destDirectory))) {

			File fileToZip = new File(inputDir);
			zipFileInDir(fileToZip, fileToZip.getName(), zipOut);

		} catch (java.io.FileNotFoundException e) {
			throw new FileNotFoundException(ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(ZipUtilConstants.IO_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
		return true;
	}

	/**
	 * Inner method of zipDirectory Method, called for zip all files of the given
	 * Directory
	 * 
	 * @param fileToZip
	 *            files from given Directory
	 * @param fileName
	 *            file names from given Directory
	 * @return true if zip created in directory
	 * @throws FileNotFoundException
	 *             when file is not found
	 * @throws IOException
	 *             when file unable to read
	 */

	private static boolean zipFileInDir(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {

		if (fileToZip.isHidden()) {
			return false;
		}
		if (fileToZip.isDirectory()) {
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFileInDir(childFile, fileName + File.separator + childFile.getName(), zipOut);
			}
			return false;
		}
		try (FileInputStream fis = new FileInputStream(fileToZip)) {
			ZipEntry zipEntry = new ZipEntry(fileName);
			zipOut.putNextEntry(zipEntry);
			readFile(zipOut, fis);
		} catch (java.io.FileNotFoundException e) {
			throw new FileNotFoundException(ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(ZipUtilConstants.IO_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
		return true;
	}

	/**
	 * This method to UnZip files from Zipped File. It will unZip only zip files,
	 * not zippedDir.
	 * 
	 * @param inputZipFile
	 *            pass Zip file address as String example : String inputDir =
	 *            "D:\\Testfiles\\compressedDir.zip";
	 * 
	 * @param outputUnZip
	 *            pass UpZipfile address as string example : String inputDir =
	 *            "D:\\Testfiles\\unzip";
	 * 
	 * @return true if given zipped file is unziped
	 * 
	 * @throws FileNotFoundException
	 *             when file is not found
	 * 
	 * @throws IOException
	 *             when file unable to read
	 */

	public static boolean unZipFile(String inputZipFile, String outputUnZip) throws IOException {

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(inputZipFile))) {
			ZipEntry zipEntry = zis.getNextEntry();

			while (zipEntry != null) {
				String fileName = zipEntry.getName();
				File newFile = new File(outputUnZip + fileName);
				createOutputFile(zis, newFile);
				zipEntry = zis.getNextEntry();
			}
		} catch (java.io.FileNotFoundException e) {
			throw new FileNotFoundException(ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(ZipUtilConstants.IO_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
		return true;
	}

	/**
	 * This is inner method for unZipFile method used for created output folder
	 *
	 * @param zipInStream
	 *            next Entry inside the zip folder
	 * @param newFile
	 *            output unZip file
	 * @throws FileNotFoundException
	 *             when file is not found
	 * @throws IOException
	 *             when file unable to read
	 */
	private static void createOutputFile(ZipInputStream zipInStream, File newFile)
			throws java.io.IOException, FileNotFoundException {

		byte[] buffer = new byte[1024];
		try (FileOutputStream fos = new FileOutputStream(newFile)) {
			int len;
			while ((len = zipInStream.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} catch (java.io.FileNotFoundException e) {
			throw new FileNotFoundException(ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by
	 * destDirectory (will be created if does not exists)
	 * 
	 * @param zipFilePath
	 *            input zipped directory example: zipFilePath =
	 *            "D:\\Testfiles\\test.zip";
	 * @param destDirectory
	 *            output unziped Directory example : outputUnZipDir =
	 *            "D:\\Testfiles\\unZipDir";
	 * 
	 * @return true zip file extracted in given directory
	 * 
	 * @throws FileNotFoundException
	 *             when file is not found
	 * @throws IOException
	 *             when file unable to read
	 */

	public static boolean unZipDirectory(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}

		try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {

			ZipEntry entry = zipIn.getNextEntry();
			while (entry != null) {
				String filePath = destDirectory + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					new File(filePath).getParentFile().mkdirs();
					extractFile(zipIn, filePath);
				} else {
					File dir = new File(filePath);
					dir.mkdirs();
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
		} catch (java.io.FileNotFoundException e) {
			throw new FileNotFoundException(ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(ZipUtilConstants.IO_ERROR_CODE.getErrorCode(),
					ZipUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}

		return true;
	}

	/**
	 * This is inner method for Extracts a zip entry (file entry)
	 * 
	 * @param zipIn
	 *            Inner entries
	 * @param filePath
	 *            output Directory
	 * @throws IOException
	 *             when file unable to read
	 */
	private static void extractFile(ZipInputStream zipIn, String filePath) throws java.io.IOException {
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
			byte[] bytesIn = new byte[10000];
			int read = 0;
			while ((read = zipIn.read(bytesIn)) != -1) {
				bos.write(bytesIn, 0, read);
			}
		}
	}
}
