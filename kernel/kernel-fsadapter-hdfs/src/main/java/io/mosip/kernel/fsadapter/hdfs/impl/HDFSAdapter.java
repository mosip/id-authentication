package io.mosip.kernel.fsadapter.hdfs.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.fsadapter.hdfs.constant.HDFSAdapterErrorCode;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtil;

/**
 * HDFS Adapter implementation for accessing the Hadoop Distributed File System
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class HDFSAdapter implements FileSystemAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(HDFSAdapter.class.getName());

	/**
	 * The field for Hadoop filesystem
	 */
	private FileSystem fs;

	/**
	 * Constructor to initalize HDFSAdapter by injecting {@link ConnectionUtil}
	 * 
	 * @param connectionUtil
	 *            connectionUtil instanse
	 */
	public HDFSAdapter(ConnectionUtil connectionUtil) {
		if (fs == null) {
			fs = connectionUtil.getConfiguredFileSystem();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#checkFileExistence(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public boolean checkFileExistence(String id, String filePath) {
		LOGGER.info("Checking if file exist in packet {} with path {}", id, getFilePath(filePath));
		Path path = getHadoopPath(id, filePath);
		try {
			return fs.exists(path);
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#copyFile(java.lang.
	 * String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean copyFile(String sourcePacket, String sourceFilePath, String destinationPacket,
			String destinationFilePath) {
		LOGGER.info("Copying file from packet {} with path {} to packet {} with path {}", sourcePacket, sourceFilePath,
				destinationPacket, destinationFilePath);
		return storeFile(destinationPacket, destinationFilePath, getFile(sourcePacket, sourceFilePath));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#deleteFile(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public boolean deleteFile(String id, String filePath) {
		LOGGER.info("Deleting file in packet {} with path {}", id, getFilePath(filePath));
		Path path = getHadoopPath(id, filePath);
		try {
			return fs.delete(path, true);
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#deletePacket(java.lang.
	 * String)
	 */
	@Override
	public boolean deletePacket(String id) {
		LOGGER.info("Deleting packet {}", id);
		Path path = getHadoopPath(id, id);
		try {
			return fs.delete(path, true);
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#getFile(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public InputStream getFile(String id, String filePath) {
		LOGGER.info("Getting file from packet {} with path {}", id, getFilePath(filePath));
		Path inFile = getHadoopPath(id, filePath);
		try {
			if (!fs.exists(inFile)) {
				throw new FSAdapterException(HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorCode(),
						HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
			return fs.open(inFile);
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#getPacket(java.lang.
	 * String)
	 */
	@Override
	public InputStream getPacket(String id) {
		LOGGER.info("Getting packet {} ", id);
		try {
			Path inFile = getHadoopPath(id, id);
			if (!fs.exists(inFile)) {
				throw new FSAdapterException(HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorCode(),
						HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
			return fs.open(inFile);
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#isPacketPresent(java.
	 * lang.String)
	 */
	@Override
	public boolean isPacketPresent(String id) {
		LOGGER.info("Checking if packet {} exists", id);
		Path path = getHadoopPath(id, id);
		try {
			return fs.exists(path);
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#storeFile(java.lang.
	 * String, java.lang.String, java.io.InputStream)
	 */
	@Override
	public boolean storeFile(String id, String filePath, InputStream content) {
		LOGGER.info("Storing file in packet {} with path {}", id, getFilePath(filePath));
		Path path = getHadoopPath(id, filePath);
		FSDataOutputStream out = null;
		try {
			out = fs.create(path);
			IOUtils.copyBytes(content, out, 512, false);
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		} finally {
			IOUtils.closeStream(out);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#storePacket(java.lang.
	 * String, java.io.File)
	 */
	@Override
	public boolean storePacket(String id, File file) {
		LOGGER.info("Storing packet {}", id);
		Path path = getHadoopPath(id, id);
		FSDataOutputStream out = null;
		try {
			out = fs.create(path);
			IOUtils.copyBytes(FileUtils.openInputStream(file), out, 512, false);
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		} finally {
			IOUtils.closeStream(out);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#storePacket(java.lang.
	 * String, java.io.InputStream)
	 */
	@Override
	public boolean storePacket(String id, InputStream content) {
		LOGGER.info("Storing packet {}", id);
		Path path = getHadoopPath(id, id);
		FSDataOutputStream out = null;
		try {
			out = fs.create(path);
			IOUtils.copyBytes(content, out, 512, false);
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		} finally {
			IOUtils.closeStream(out);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#unpackPacket(java.lang.
	 * String)
	 */
	@Override
	public void unpackPacket(String id) {
		LOGGER.info("Unpacking packet {}", id);
		InputStream packetStream = getPacket(id);
		ZipInputStream zis = new ZipInputStream(packetStream);
		byte[] buffer = new byte[2048];
		byte[] file;
		try {
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int len;
				while ((len = zis.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
				file = out.toByteArray();
				InputStream inputStream = new ByteArrayInputStream(file);
				String filePath = FilenameUtils.getPathNoEndSeparator(ze.getName());
				String fileName = FilenameUtils.getBaseName(ze.getName());
				if (!fileName.isEmpty()) {
					storeFile(id, FilenameUtils.concat(filePath, fileName), inputStream);
				}
				inputStream.close();
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		}
	}

	/**
	 * Construct a hadoop path from a String
	 * 
	 * @param id
	 *            the packetId
	 * @param filePath
	 *            the filePath
	 * @return the path
	 */
	public Path getHadoopPath(String id, String filePath) {
		return new Path(FilenameUtils.concat(getFilePath(id), getFilePath(filePath)));
	}

	/**
	 * Get formatted filePath
	 * 
	 * @param filePath
	 *            filePath
	 * @return formatted filePath
	 */
	public String getFilePath(String filePath) {
		return filePath.toUpperCase();
	}
}