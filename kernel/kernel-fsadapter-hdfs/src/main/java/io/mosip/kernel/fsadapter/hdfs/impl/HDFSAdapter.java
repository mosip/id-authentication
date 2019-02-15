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

	/**
	 * The field for Hadoop filesystem
	 */
	private final FileSystem fs;

	/**
	 * Constructor to initalize HDFSAdapter by injecting {@link ConnectionUtil}
	 * 
	 * @param connectionUtil
	 *            connectionUtil instanse
	 */
	public HDFSAdapter(ConnectionUtil connectionUtil) {
		fs = connectionUtil.getConfiguredFileSystem();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#checkFileExistence(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public boolean checkFileExistence(String id, String fileName) {
		Path path = new Path(FilenameUtils.concat(id, fileName));
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
	public boolean copyFile(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) {
		return storeFile(destinationBucketName, destinationKey, getFile(sourceBucketName, sourceKey));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.fsadapter.hdfs.spi.FileSystemAdapter#deleteFile(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public boolean deleteFile(String id, String fileName) {
		Path path = new Path(FilenameUtils.concat(id, fileName));
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
		Path path = new Path(FilenameUtils.concat(id, id));
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
	public InputStream getFile(String id, String fileName) {
		Path inFile = new Path(FilenameUtils.concat(id, fileName.toUpperCase()));
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
		try {
			Path inFile = new Path(FilenameUtils.concat(id, id));
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
		Path path = new Path(FilenameUtils.concat(id, id));
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
	public boolean storeFile(String id, String key, InputStream content) {
		Path path = new Path(FilenameUtils.concat(id, key));
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
		Path path = new Path(FilenameUtils.concat(id, id));
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
		Path path = new Path(FilenameUtils.concat(id, id));
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
					storeFile(FilenameUtils.concat(id, filePath.toUpperCase()), fileName.toUpperCase(), inputStream);
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
}