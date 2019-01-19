package io.mosip.kernel.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Checksum;

import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.IOFileFilter;

import io.mosip.kernel.core.exception.FileExistsException;
import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.NullPointerException;
import io.mosip.kernel.core.exception.UnsupportedCharsetException;
import io.mosip.kernel.core.exception.UnsupportedEncodingException;
import io.mosip.kernel.core.util.constant.FileUtilConstants;

/**
 * This class defines the File Utils to be used in MOSIP Project The File Utils
 * are implemented using methods of org.apache.commons.io.FileUtils class of
 * Apache commons.io package
 * 
 * @author Priya Soni
 * @since 1.0.0
 *
 */

public class FileUtils {

	/**
	 * Constructor for this class
	 */
	private FileUtils() {

	}

	/**
	 * Returns a human-readable version of the file size, where the input represents
	 * a specific number of bytes.
	 * 
	 * @param size
	 *            Size of the file
	 * @return a human-readable version of the file size
	 */

	public static String byteCountToDisplaySize(long size) {
		return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);
	}

	/**
	 * Computes the checksum of a file using the specified checksum object
	 * 
	 * @param file
	 *            Input file to checksum
	 * @param checksum
	 *            The checksum object to be used
	 * @return value of the checksum
	 * @throws IllegalArgumentException
	 *             if the file is a directory.
	 * @throws IOException
	 *             if an IO error occurs reading the file
	 * @throws NullPointerException
	 *             if the file or checksum is null
	 * 
	 */
	public static Checksum checksum(File file, Checksum checksum) throws IOException {

		try {
			return org.apache.commons.io.FileUtils.checksum(file, checksum);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * 
	 * Computes the checksum of a file using the CRC32 checksum routine
	 * 
	 * @param file
	 *            Input file to checksum
	 * @return value of the checksum
	 * @throws IllegalArgumentException
	 *             if the file is a directory.
	 * @throws IOException
	 *             if an IO error occurs reading the file.
	 * @throws NullPointerException
	 *             if the file or checksum is null.
	 */
	public static long checksumCRC32(File file) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.checksumCRC32(file);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Cleans a directory without deleting it
	 * 
	 * @param directory
	 *            Input directory to clean
	 * @throws IOException
	 *             in case cleaning is unsuccessful.
	 * @throws IllegalArgumentException
	 *             if directory does not exist or is not a directory.
	 */
	public static void cleanDirectory(File directory) throws IOException {
		try {
			org.apache.commons.io.FileUtils.cleanDirectory(directory);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * 
	 * Compares the contents of two files to determine if they are equal or not
	 * 
	 * @param file1
	 *            the first file
	 * @param file2
	 *            the second file
	 * @return true if the content of the files are equal or they both don't exist,
	 *         false otherwise
	 * @throws IOException
	 *             in case of an I/O error.
	 *
	 */
	public static boolean contentEquals(File file1, File file2) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.contentEquals(file1, file2);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Compares the contents of two files to determine if they are equal or not.
	 * This method checks to see if the two files point to the same file, before
	 * resorting to line-by-line comparison of the contents.
	 * 
	 * @param file1
	 *            the first file
	 * @param file2
	 *            the second file
	 * @param charsetName
	 *            the character encoding to be used
	 * @return true if the content of the files are equal or neither exists, false
	 *         otherwise
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static boolean contentEqualsIgnoreEOL(File file1, File file2, String charsetName) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.contentEqualsIgnoreEOL(file1, file2, charsetName);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Converts a Collection containing java.io.File instanced into array
	 * representation
	 * 
	 * @param files
	 *            a Collection containing java.io.File instances
	 * @return an array of java.io.File
	 */
	public static File[] convertFileCollectionToFileArray(Collection<File> files) {
		return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(files);
	}

	/**
	 * 
	 * Copies a whole directory to a new location preserving the file dates
	 * 
	 * @param srcDir
	 *            an existing directory to copy
	 * @param destDir
	 *            the new directory
	 * @throws NullPointerException
	 *             if source or destination is null.
	 * @throws IOException
	 *             if source or destination is invalid or if an IO error occurs
	 *             during copying.
	 * 
	 */
	public static void copyDirectory(File srcDir, File destDir) throws IOException {
		try {
			org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Copies a file to a new location preserving the file date
	 * 
	 * @param srcFile
	 *            an existing file to copy
	 * @param destFile
	 *            the new file
	 * @throws NullPointerException
	 *             if source or destination is null.
	 * @throws IOException
	 *             if source or destination is invalid or if an IO error occurs
	 *             during copying or if the output file length is not the same as
	 *             the input file length after the copy completes.
	 * 
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException {
		try {
			org.apache.commons.io.FileUtils.copyFile(srcFile, destFile);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Copy bytes from a File to an OutputStream.
	 * 
	 * @param input
	 *            the file to read from
	 * @param output
	 *            the OutputStream to write to
	 * @return the number of bytes copied
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws NullPointerException
	 *             if the input or output is null.
	 * 
	 */
	public static long copyFile(File input, OutputStream output) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.copyFile(input, output);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Copies bytes from an InputStream source to a file destination.
	 * 
	 * @param source
	 *            the InputStream to copy bytes from
	 * @param destination
	 *            the non-directory File to write bytes to (possibly overwriting)
	 * @throws IOException
	 *             if destination is a directory or if destination cannot be written
	 *             or if destination needs creating but can't be or if an IO error
	 *             occurs during copying.
	 *
	 */
	public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
		try {
			org.apache.commons.io.FileUtils.copyInputStreamToFile(source, destination);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Copies bytes from an InputStream source to a file destination The source
	 * stream is left open
	 * 
	 * @param source
	 *            the InputStream to copy bytes from
	 * @param destination
	 *            the non-directory File to write bytes to (possibly overwriting)
	 * @throws IOException
	 *             if destination is a directory or if destination cannot be written
	 *             or if destination needs creating but can't be or if an IO error
	 *             occurs during copying.
	 * 
	 */
	public static void copyToFile(InputStream source, File destination) throws IOException {
		try {
			org.apache.commons.io.FileUtils.copyToFile(source, destination);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Deletes a directory recursively
	 * 
	 * @param directory
	 *            directory to delete
	 * @throws IOException
	 *             in case deletion is unsuccessful.
	 * @throws IllegalArgumentException
	 *             if directory does not exist or is not a directory.
	 */
	public static void deleteDirectory(File directory) throws IOException {
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(directory);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Deletes a file, never throwing an exception
	 * 
	 * @param file
	 *            file or directory to delete
	 * @return true if the file or directory was deleted, otherwise false
	 */
	public static boolean deleteQuietly(File file) {
		return org.apache.commons.io.FileUtils.deleteQuietly(file);
	}

	/**
	 * Determines whether the parent directory contains the child element (a file or
	 * directory)
	 * 
	 * @param directory
	 *            the parent directory
	 * @param child
	 *            the child file or directory
	 * @return true is the candidate leaf is under by the specified composite. False
	 *         otherwise
	 * @throws IOException
	 *             if an IO error occurs while checking the files.
	 * @throws IllegalArgumentException
	 *             if directory is null or not a directory.
	 */
	public static boolean directoryContains(File directory, File child) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.directoryContains(directory, child);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Deletes a file.
	 * 
	 * @param file
	 *            file or directory to delete
	 * @throws IOException
	 *             in case deletion is unsuccessful.
	 * @throws FileNotFoundException
	 *             if the file was not found
	 * @throws NullPointerException
	 *             if the directory is null.
	 */
	public static void forceDelete(File file) throws IOException {
		try {
			org.apache.commons.io.FileUtils.forceDelete(file);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.FileNotFoundException e) {
			throw new FileNotFoundException(FileUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					FileUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Schedules a file to be deleted when JVM exits
	 * 
	 * @param file
	 *            file or directory to delete
	 * @throws IOException
	 *             in case deletion is unsuccessful.
	 * @throws NullPointerException
	 *             if the file is null.
	 */
	public static void forceDeleteOnExit(File file) throws IOException {
		try {
			org.apache.commons.io.FileUtils.forceDeleteOnExit(file);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Construct a file from the set of name elements.
	 * 
	 * @param directory
	 *            the parent directory
	 * @param names
	 *            the name elements
	 * @return file
	 */
	public static File getFile(File directory, String... names) {
		return org.apache.commons.io.FileUtils.getFile(directory, names);
	}

	/**
	 * Construct a file from the set of name elements.
	 * 
	 * @param names
	 *            the name elements
	 * @return file
	 */
	public static File getFile(String... names) {
		return org.apache.commons.io.FileUtils.getFile(names);
	}

	/**
	 * Tests if the specified File is newer than the specified Date
	 * 
	 * @param file
	 *            the File of which the modification date must be compared
	 * @param date
	 *            the date reference
	 * @throws IllegalArgumentException
	 *             if the file is null or if the date is null.
	 * @return true if the File exists and has been modified after the given Date
	 */
	public static boolean isFileNewer(File file, Date date) {
		try {
			return org.apache.commons.io.FileUtils.isFileNewer(file, date);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getMessage(), e.getCause());
		}

	}

	/**
	 * Tests if the specified File is older than the specified Date
	 * 
	 * @param file
	 *            the File of which the modification date must be compared
	 * @param date
	 *            the date reference
	 * @throws IllegalArgumentException
	 *             if the file is null or if the date is null.
	 * @return true if the File exists and has been modified before the given Date
	 */
	public static boolean isFileOlder(File file, Date date) {
		try {
			return org.apache.commons.io.FileUtils.isFileOlder(file, date);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Determines whether the specified file is a Symbolic Link rather than an
	 * actual file
	 * 
	 * @param file
	 *            the file to check
	 * @return true if the file is a Symbolic Link
	 * @throws IOException
	 *             if an IO error occurs while checking the file.
	 */
	public static boolean isSymlink(File file) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.isSymlink(file);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Allows iteration over the files in given directory (and optionally its sub
	 * directories). All files found are filtered by an IOFileFilter
	 * 
	 * @param directory
	 *            the directory to search in
	 * @param fileFilter
	 *            filter to apply when finding files
	 * @param dirFilter
	 *            optional filter to apply when finding subdirectories
	 * @return an iterator of java.io.File for the matching files
	 */
	public static Iterator<File> iterateFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
		return org.apache.commons.io.FileUtils.iterateFiles(directory, fileFilter, dirFilter);
	}

	/**
	 * Returns an Iterator for the lines in a File using the default encoding for
	 * the VM
	 * 
	 * @param file
	 *            the file to open for input
	 * @return an Iterator of the lines in the file, never null
	 * @throws IOException
	 *             in case of an I/O error (file closed).
	 * 
	 */
	public static LineIterator lineIterator(File file) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.lineIterator(file);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Returns an Iterator for the lines in a File
	 * 
	 * @param file
	 *            the file to open for input
	 * @param encoding
	 *            the encoding to use
	 * @return an Iterator of the lines in the file, never null
	 * @throws IOException
	 *             in case of an I/O error (file closed).
	 * 
	 */
	public static LineIterator lineIterator(File file, String encoding) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.lineIterator(file, encoding);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Finds files within a given directory (and optionally its subdirectories). All
	 * files found are filtered by an IOFileFilter.
	 * 
	 * @param directory
	 *            the directory to search in
	 * @param fileFilter
	 *            filter to apply when finding files
	 * @param dirFilter
	 *            optional filter to apply when finding subdirectories
	 * @return an collection of java.io.File with the matching files
	 */
	public static Collection<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
		return org.apache.commons.io.FileUtils.listFiles(directory, fileFilter, dirFilter);
	}

	/**
	 * Finds files within a given directory (and optionally its subdirectories) All
	 * files found are filtered by an IOFileFilter. The resulting collection
	 * includes the starting directory and any subdirectories that match the
	 * directory filter
	 * 
	 * @param directory
	 *            the directory to search in
	 * @param fileFilter
	 *            filter to apply when finding files
	 * @param dirFilter
	 *            optional filter to apply when finding subdirectories
	 * @return an collection of java.io.File with the matching files
	 */
	public static Collection<File> listFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
		return org.apache.commons.io.FileUtils.listFilesAndDirs(directory, fileFilter, dirFilter);
	}

	/**
	 * Moves a directory
	 * 
	 * @param srcDir
	 *            the directory to be moved
	 * @param destDir
	 *            the destination directory
	 * @throws FileExistsException
	 *             if the destination directory exists.
	 * @throws IOException
	 *             if an IO error occurs moving the file or if source or destination
	 *             is invalid.
	 * @throws NullPointerException
	 *             if source or destination is null.
	 */
	public static void moveDirectory(File srcDir, File destDir) throws IOException {
		try {
			org.apache.commons.io.FileUtils.moveDirectory(srcDir, destDir);

		} catch (org.apache.commons.io.FileExistsException e) {
			throw new FileExistsException(FileUtilConstants.FILE_EXISTS_ERROR_CODE.getErrorCode(),
					FileUtilConstants.FILE_EXISTS_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Moves a directory to another directory
	 * 
	 * @param src
	 *            the file to be moved
	 * @param destDir
	 *            the destination file
	 * @param createDestDir
	 *            If true create the destination directory, otherwise if false throw
	 *            an java.io.IOException
	 * @throws FileExistsException
	 *             if the directory exists in the destination directory.
	 * @throws IOException
	 *             if source or destination is invalid or if an IO error occurs
	 *             moving the file.
	 * @throws NullPointerException
	 *             if source or destination is null.
	 */
	public static void moveDirectoryToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
		try {
			org.apache.commons.io.FileUtils.moveDirectoryToDirectory(src, destDir, createDestDir);
		} catch (org.apache.commons.io.FileExistsException e) {
			throw new FileExistsException(FileUtilConstants.FILE_EXISTS_ERROR_CODE.getErrorCode(),
					FileUtilConstants.FILE_EXISTS_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Moves a file
	 * 
	 * @param srcFile
	 *            the file to be moved
	 * @param destFile
	 *            the destination file
	 * @throws FileExistsException
	 *             if the destination file exists.
	 * @throws IOException
	 *             if source or destination is invalid or if an IO error occurs
	 *             moving the file.
	 * @throws NullPointerException
	 *             if source or destination is null.
	 */
	public static void moveFile(File srcFile, File destFile) throws IOException {
		try {
			org.apache.commons.io.FileUtils.moveFile(srcFile, destFile);
		} catch (org.apache.commons.io.FileExistsException e) {
			throw new FileExistsException(FileUtilConstants.FILE_EXISTS_ERROR_CODE.getErrorCode(),
					FileUtilConstants.FILE_EXISTS_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Moves a file to a directory
	 * 
	 * @param srcFile
	 *            the file to be moved
	 * @param destDir
	 *            he destination file
	 * @param createDestDir
	 *            If true create the destination directory, otherwise if false throw
	 *            an java.io.IOException
	 * @throws FileExistsException
	 *             if the destination file exists.
	 * @throws IOException
	 *             if source or destination is invalid or if an IO error occurs
	 *             moving the file.
	 * @throws NullPointerException
	 *             if source or destination is null.
	 * 
	 */
	public static void moveFileToDirectory(File srcFile, File destDir, boolean createDestDir) throws IOException {
		try {
			org.apache.commons.io.FileUtils.moveFileToDirectory(srcFile, destDir, createDestDir);
		} catch (org.apache.commons.io.FileExistsException e) {
			throw new FileExistsException(FileUtilConstants.FILE_EXISTS_ERROR_CODE.getErrorCode(),
					FileUtilConstants.FILE_EXISTS_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Moves a file or directory to the destination directory
	 * 
	 * @param src
	 *            the file or directory to be moved
	 * @param destDir
	 *            the destination directory
	 * @param createDestDir
	 *            If true create the destination directory, otherwise if false throw
	 *            an java.io.IOException
	 * @throws FileExistsException
	 *             if the directory or file exists in the destination directory.
	 * @throws IOException
	 *             if an IO error occurs moving the file or if source or destination
	 *             is invalid.
	 * @throws NullPointerException
	 *             if source or destination is null.
	 */
	public static void moveToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
		try {
			org.apache.commons.io.FileUtils.moveToDirectory(src, destDir, createDestDir);
		} catch (org.apache.commons.io.FileExistsException e) {
			throw new FileExistsException(FileUtilConstants.FILE_EXISTS_ERROR_CODE.getErrorCode(),
					FileUtilConstants.FILE_EXISTS_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Opens a FileInputStream for the specified file,
	 * 
	 * @param file
	 *            the file to open for input
	 * @return a new FileInputStream for the specified file
	 * @throws FileNotFoundException
	 *             if the file does not exist.
	 * @throws IOException
	 *             if the file cannot be read.
	 *
	 */
	public static FileInputStream openInputStream(File file) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.openInputStream(file);
		} catch (java.io.FileNotFoundException e) {
			throw new FileNotFoundException(FileUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					FileUtilConstants.FILE_NOT_FOUND_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Opens a FileOutputStream for the specified file, checking and creating the
	 * parent directory if it does not exist.
	 * 
	 * @param file
	 *            the file to open for output
	 * @return a new FileOutputStream for the specified file
	 * @throws IOException
	 *             if the file object is a directory or if a parent directory needs
	 *             creating but that fails or if the file cannot be written to.
	 *
	 */
	public static FileOutputStream openOutputStream(File file) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.openOutputStream(file);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * 
	 * Opens a FileOutputStream for the specified file, checking and creating the
	 * parent directory if it does not exist.
	 * 
	 * @param file
	 *            the file to open for output
	 * @param append
	 *            if true, then bytes will be added to the end of the file rather
	 *            than overwriting
	 * @return a new FileOutputStream for the specified file
	 * @throws IOException
	 *             if the file object is a directory or if a parent directory needs
	 *             creating but that fails or if the file cannot be written to.
	 * 
	 */
	public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.openOutputStream(file, append);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Reads the contents of a file into a byte array
	 * 
	 * @param file
	 *            the file to read
	 * @return the file contents
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static byte[] readFileToByteArray(File file) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.readFileToByteArray(file);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Reads the contents of a file into a String. The file is always closed.
	 * 
	 * @param file
	 *            the file to read
	 * @param encoding
	 *            the encoding to use
	 * @return the file contents
	 * @throws IOException
	 *             in case of an I/O error.
	 *
	 */
	public static String readFileToString(File file, Charset encoding) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.readFileToString(file, encoding);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * 
	 * Reads the contents of a file line by line to a List of Strings The file is
	 * always closed
	 * 
	 * @param file
	 *            the file to read
	 * @param encoding
	 *            the encoding to use
	 * @return the list of Strings representing each line in the file, never null
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static List<String> readLines(File file, Charset encoding) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.readLines(file, encoding);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * 
	 * Reads the contents of a file line by line to a List of Strings The file is
	 * always closed.
	 * 
	 * @param file
	 *            the file to read
	 * @param encoding
	 *            the encoding to use
	 * @return the list of Strings representing each line in the file, never null
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedCharsetException
	 *             thrown instead of .UnsupportedEncodingException in version 2.2 if
	 *             the encoding is not supported.
	 */
	public static List<String> readLines(File file, String encoding) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.readLines(file, encoding);
		} catch (java.nio.charset.UnsupportedCharsetException e) {
			throw new UnsupportedCharsetException(FileUtilConstants.UNSUPPORTED_CHARSET_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_CHARSET_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Returns the size of the specified file or directory The return value may be
	 * negative if overflow occurs
	 * 
	 * @param file
	 *            the file or directory to return the size of
	 * @throws NullPointerException
	 *             if the file is null.
	 * @throws IllegalArgumentException
	 *             if the file does not exist.
	 * @return the length of the file, or recursive size of the directory, provided
	 *         (in bytes)
	 */
	public static long sizeOf(File file) {
		try {
			return org.apache.commons.io.FileUtils.sizeOf(file);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getMessage(), e.getCause());
		}

	}

	/**
	 * Counts the size of a directory recursively (sum of the length of all files)
	 * 
	 * @param directory
	 *            directory to inspect
	 * @return size of directory in bytes
	 * @throws NullPointerException
	 *             if the directory is null.
	 */
	public static long sizeOfDirectory(File directory) {
		try {
			return org.apache.commons.io.FileUtils.sizeOfDirectory(directory);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		}

	}

	/**
	 * Convert from a URL to a File
	 * 
	 * @param url
	 *            the file URL to convert
	 * @return the equivalent File object, or null if the URL's protocol is not file
	 */
	public static File toFile(URL url) {
		return org.apache.commons.io.FileUtils.toFile(url);
	}

	/**
	 * Converts each of an array of URL to a File.
	 * 
	 * @param urls
	 *            the file URLs to convert,
	 * @return a non-null array of Files matching the input, with a null item if
	 *         there was a null at that index in the input array
	 * @throws IllegalArgumentException
	 *             if any file is not a URL file or if any file is incorrectly
	 *             encoded.
	 */
	public static File[] toFiles(URL[] urls) {
		try {
			return org.apache.commons.io.FileUtils.toFiles(urls);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					FileUtilConstants.ILLEGAL_ARGUMENT_ERROR_CODE.getMessage(), e.getCause());
		}

	}

	/**
	 * 
	 * Converts each of an array of File to a URL.
	 * 
	 * @param files
	 *            the files to convert
	 * @return an array of URLs matching the input
	 * @throws IOException
	 *             if a file cannot be converted.
	 * @throws NullPointerException
	 *             if the parameter is null.
	 */
	public static URL[] toURLs(File[] files) throws IOException {
		try {
			return org.apache.commons.io.FileUtils.toURLs(files);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Waits for NFS to propagate a file creation, imposing a timeout.
	 * 
	 * @param file
	 *            the file to check
	 * @param seconds
	 *            the maximum time in seconds to wait
	 * @return true if file exists
	 * @throws NullPointerException
	 *             if the file is null.
	 */
	public static boolean waitFor(File file, int seconds) {
		try {
			return org.apache.commons.io.FileUtils.waitFor(file, seconds);
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(FileUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					FileUtilConstants.NULL_POINTER_ERROR_CODE.getMessage(), e.getCause());
		}

	}

	/**
	 * Writes a CharSequence to a file creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write
	 * @param data
	 *            the content to write to the file
	 * @param encoding
	 *            the encoding to use
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedEncodingException
	 *             if the encoding is not supported by the VM.
	 */
	public static void write(File file, CharSequence data, String encoding) throws IOException {
		try {
			org.apache.commons.io.FileUtils.write(file, data, encoding);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException(FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes a CharSequence to a file creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write
	 * @param data
	 *            the content to write to the file
	 * @param encoding
	 *            the encoding to use
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static void write(File file, CharSequence data, Charset encoding) throws IOException {
		try {
			org.apache.commons.io.FileUtils.write(file, data, encoding);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes a CharSequence to a file creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write
	 * @param data
	 *            the content to write to the file
	 * @param encoding
	 *            the encoding to use
	 * @param append
	 *            if true, then the data will be added to the end of the file rather
	 *            than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static void write(File file, CharSequence data, Charset encoding, boolean append) throws IOException {
		try {
			org.apache.commons.io.FileUtils.write(file, data, encoding, append);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes a CharSequence to a file creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write
	 * @param data
	 *            the content to write to the file
	 * @param encoding
	 *            the encoding to use
	 * @param append
	 *            if true, then the data will be added to the end of the file rather
	 *            than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedCharsetException
	 *             if the encoding is not supported by the VM.
	 * 
	 */
	public static void write(File file, CharSequence data, String encoding, boolean append) throws IOException {
		try {
			org.apache.commons.io.FileUtils.write(file, data, encoding, append);
		} catch (java.nio.charset.UnsupportedCharsetException e) {
			throw new UnsupportedCharsetException(FileUtilConstants.UNSUPPORTED_CHARSET_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_CHARSET_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes a byte array to a file creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write to
	 * @param data
	 *            the content to write to the file
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes a byte array to a file creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write to
	 * @param data
	 *            the content to write to the file
	 * @param append
	 *            if true, then bytes will be added to the end of the file rather
	 *            than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 *
	 */
	public static void writeByteArrayToFile(File file, byte[] data, boolean append) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data, append);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes len bytes from the specified byte array starting at offset off to a
	 * file, creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write to
	 * @param data
	 *            the content to write to the file
	 * @param off
	 *            the start offset in the data
	 * @param len
	 *            the number of bytes to write
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static void writeByteArrayToFile(File file, byte[] data, int off, int len) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data, off, len);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes len bytes from the specified byte array starting at offset off to a
	 * file, creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write to
	 * @param data
	 *            the content to write to the file
	 * @param off
	 *            the start offset in the data
	 * @param len
	 *            the number of bytes to write
	 * 
	 * @param append
	 *            if true, then bytes will be added to the end of the file rather
	 *            than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static void writeByteArrayToFile(File file, byte[] data, int off, int len, boolean append)
			throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data, off, len, append);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes the toString() value of each item in a collection to the specified
	 * File line by line. The default VM encoding and the default line ending will
	 * be used.
	 * 
	 * @param file
	 *            the file to write to
	 * @param lines
	 *            the lines to write
	 * @throws IOException
	 *             in case of an I/O error.
	 *
	 */
	public static void writeLines(File file, Collection<?> lines) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeLines(file, lines);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes the toString() value of each item in a collection to the specified
	 * File line by line. The default VM encoding and the default line ending will
	 * be used.
	 * 
	 * @param file
	 *            the file to write to
	 * @param lines
	 *            the lines to write
	 * @param append
	 *            if true, then the lines will be added to the end of the file
	 *            rather than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static void writeLines(File file, Collection<?> lines, boolean append) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeLines(file, lines, append);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes the toString() value of each item in a collection to the specified
	 * File line by line. The specified character encoding and the line ending will
	 * be used.
	 * 
	 * @param file
	 *            the file to write to
	 * @param encoding
	 *            the encoding to use
	 * @param lines
	 *            the lines to write
	 * @param lineEnding
	 *            the line separator to use
	 * @param append
	 *            if true, then the lines will be added to the end of the file
	 *            rather than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedEncodingException
	 *             if the encoding is not supported by the VM.
	 */
	public static void writeLines(File file, String encoding, Collection<?> lines, String lineEnding, boolean append)
			throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeLines(file, encoding, lines, lineEnding, append);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException(FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes the toString() value of each item in a collection to the specified
	 * File line by line. The default VM encoding and the specified line ending will
	 * be used.
	 * 
	 * @param file
	 *            the file to write to
	 * @param lines
	 *            the lines to write
	 * @param lineEnding
	 *            the line separator to use
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static void writeLines(File file, Collection<?> lines, String lineEnding) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeLines(file, lines, lineEnding);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes the toString() value of each item in a collection to the specified
	 * File line by line. The specified character encoding and the line ending will
	 * be used.
	 * 
	 * @param file
	 *            the file to write to
	 * @param encoding
	 *            the encoding to use
	 * @param lines
	 *            the lines to write
	 * @param lineEnding
	 *            the line separator to use
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedEncodingException
	 *             if the encoding is not supported by the VM.
	 * 
	 */
	public static void writeLines(File file, String encoding, Collection<?> lines, String lineEnding)
			throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeLines(file, encoding, lines, lineEnding);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException(FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes the toString() value of each item in a collection to the specified
	 * File line by line. The specified character encoding and the default line
	 * ending will be used.
	 * 
	 * @param file
	 *            the file to write to
	 * @param encoding
	 *            the encoding to use
	 * @param lines
	 *            the lines to write
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedEncodingException
	 *             if the encoding is not supported by the VM.
	 */
	public static void writeLines(File file, String encoding, Collection<?> lines) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeLines(file, encoding, lines);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException(FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes the toString() value of each item in a collection to the specified
	 * File line by line, optionally appending. The specified character encoding and
	 * the default line ending will be used.
	 * 
	 * @param file
	 *            the file to write to
	 * @param encoding
	 *            the encoding to use
	 * @param lines
	 *            the lines to write
	 * @param append
	 *            if true, then the lines will be added to the end of the file
	 *            rather than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedEncodingException
	 *             if the encoding is not supported by the VM.
	 * 
	 */
	public static void writeLines(File file, String encoding, Collection<?> lines, boolean append) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeLines(file, encoding, lines, append);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException(FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes the toString() value of each item in a collection to the specified
	 * File line by line. The default VM encoding and the specified line ending will
	 * be used.
	 * 
	 * @param file
	 *            the file to write to
	 * @param lines
	 *            the lines to write
	 * @param lineEnding
	 *            the line separator to use
	 * @param append
	 *            if true, then the lines will be added to the end of the file
	 *            rather than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static void writeLines(File file, Collection<?> lines, String lineEnding, boolean append)
			throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeLines(file, lines, lineEnding, append);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes a String to a file creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write
	 * @param data
	 *            the content to write to the file
	 * @param encoding
	 *            the encoding to use
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedEncodingException
	 *             if the encoding is not supported by the VM.
	 */
	public static void writeStringToFile(File file, String data, Charset encoding) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeStringToFile(file, data, encoding);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException(FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes a String to a file creating the file if it does not exist
	 * 
	 * @param file
	 *            the file to write
	 * @param data
	 *            the content to write to the file
	 * @param encoding
	 *            the encoding to use
	 * @param append
	 *            if true, then the String will be added to the end of the file
	 *            rather than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedCharsetException
	 *             if the encoding is not supported by the VM.
	 */
	public static void writeStringToFile(File file, String data, String encoding, boolean append) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeStringToFile(file, data, encoding, append);
		} catch (java.nio.charset.UnsupportedCharsetException e) {
			throw new UnsupportedCharsetException(FileUtilConstants.UNSUPPORTED_CHARSET_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_CHARSET_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes a String to a file creating the file if it does not exist.
	 * 
	 * @param file
	 *            the file to write
	 * @param data
	 *            the content to write to the file
	 * @param encoding
	 *            the encoding to use
	 * @throws IOException
	 *             in case of an I/O error.
	 * @throws UnsupportedEncodingException
	 *             if the encoding is not supported by the VM.
	 */
	public static void writeStringToFile(File file, String data, String encoding) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeStringToFile(file, data, encoding);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException(FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getErrorCode(),
					FileUtilConstants.UNSUPPORTED_ENCODING_ERROR_CODE.getMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

	/**
	 * Writes a String to a file creating the file if it does not exist
	 * 
	 * @param file
	 *            the file to write
	 * @param data
	 *            the content to write to the file
	 * @param encoding
	 *            the encoding to use
	 * @param append
	 *            if true, then the String will be added to the end of the file
	 *            rather than overwriting
	 * @throws IOException
	 *             in case of an I/O error.
	 * 
	 */
	public static void writeStringToFile(File file, String data, Charset encoding, boolean append) throws IOException {
		try {
			org.apache.commons.io.FileUtils.writeStringToFile(file, data, encoding, append);
		} catch (java.io.IOException e) {
			throw new IOException(FileUtilConstants.IO_ERROR_CODE.getErrorCode(),
					FileUtilConstants.IO_ERROR_CODE.getMessage(), e.getCause());
		}
	}

}
