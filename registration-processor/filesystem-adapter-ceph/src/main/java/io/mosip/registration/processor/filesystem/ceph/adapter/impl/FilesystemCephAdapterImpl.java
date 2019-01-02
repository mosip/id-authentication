package io.mosip.registration.processor.filesystem.ceph.adapter.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.handler.ExceptionHandler;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.ConnectionUtil;

/**
 * This class is CEPH implementation for MOSIP Packet Store.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
@Service
public class FilesystemCephAdapterImpl implements FileSystemAdapter<InputStream, Boolean> {

	/** The conn. */
	private AmazonS3 conn;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemCephAdapterImpl.class);

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {} - {} - {}";

	/** The Constant SUCCESS_UPLOAD_MESSAGE. */
	private static final String SUCCESS_UPLOAD_MESSAGE = "uploaded to DFS successfully";

	/**
	 * Constructor to get Connection to CEPH instance.
	 *
	 * @param connectionUtil the connection util
	 */
	public FilesystemCephAdapterImpl(ConnectionUtil connectionUtil) {
		if (conn == null) {
			this.conn = connectionUtil.getConnection();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.demo.server.filehandler.FileHandler#storePacket(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Boolean storePacket(String enrolmentId, File filePath) {
		try {
			if (!conn.doesBucketExistV2(enrolmentId)) {
				conn.createBucket(enrolmentId);
			}
			this.conn.putObject(enrolmentId, enrolmentId, filePath);
			LOGGER.debug(LOGDISPLAY, enrolmentId, SUCCESS_UPLOAD_MESSAGE);
		} catch (AmazonS3Exception e) {
			LOGGER.error(LOGDISPLAY, e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			ExceptionHandler.exceptionHandler(e);
		} catch (SdkClientException e) {
			ExceptionHandler.exceptionHandler(e);
		}
		return true;
	}

	/**
	 * This method stores a packet in DFS.
	 *
	 * @param enrolmentId            The enrolment ID for the packet
	 * @param file            packet as InputStream
	 * @return True if packet is stored
	 */
	@Override
	public Boolean storePacket(String enrolmentId, InputStream file) {
		try {
			if (!conn.doesBucketExistV2(enrolmentId)) {
				conn.createBucket(enrolmentId);
			}
			this.conn.putObject(enrolmentId, enrolmentId, file, null);
			LOGGER.debug(LOGDISPLAY, enrolmentId, SUCCESS_UPLOAD_MESSAGE);
		} catch (AmazonS3Exception e) {
			LOGGER.error(LOGDISPLAY, e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			ExceptionHandler.exceptionHandler(e);
		} catch (SdkClientException e) {
			ExceptionHandler.exceptionHandler(e);
		}
		return true;
	}

//	/**
//	 * This method stores a File to DFS
//	 *
//	 * @param enrolmentId
//	 *            The enrolment ID
//	 * @param key
//	 *            The key that is to be stored
//	 * @param file
//	 *            The file to be stored
//	 * @return true if the file is stored successfully
//	 */
//	private boolean storeFile(String enrolmentId, String key, InputStream file) {
//		try {
//			this.conn.putObject(enrolmentId, key, file, null);
//			LOGGER.debug(LOGDISPLAY, enrolmentId, key, SUCCESS_UPLOAD_MESSAGE);
//		} catch (AmazonS3Exception e) {
//			LOGGER.error(LOGDISPLAY, e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
//			ExceptionHandler.exceptionHandler(e);
//		} catch (SdkClientException e) {
//			ExceptionHandler.exceptionHandler(e);
//		}
//		return true;
//	}


	/**
	 * This method stores a document in DFS
	 *
	 * @param enrolmentId
	 *            The enrolment ID for the document
	 * @param key
	 *            The key for the document
	 * @param document
	 *            document as InputStream
	 * @return True if document is stored
	 */

	@Override
	public Boolean storeFile(String enrolmentId, String key, InputStream document) {
		try {
			if (!conn.doesBucketExistV2(enrolmentId)) {
				conn.createBucket(enrolmentId);
			}
			this.conn.putObject(enrolmentId, key, document, null);
			LOGGER.debug(LOGDISPLAY, enrolmentId, key, SUCCESS_UPLOAD_MESSAGE);
		} catch (AmazonS3Exception e) {
			LOGGER.error(LOGDISPLAY, e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			ExceptionHandler.exceptionHandler(e);
		} catch (SdkClientException e) {
			ExceptionHandler.exceptionHandler(e);
		}
		return true;
	}

	/**
	 * This method copy document from one bucket to another
	 *
	 * @param sourceBucketName
	 *
	 * @param sourceKey
	 *
	 * @param destinationBucketName
	 *
	 * @param destinationKey
	 *
	 * @return True if document copy is successful
	 */

	@Override
	public Boolean copyFile(String sourceBucketName, String sourceKey,
            String destinationBucketName, String destinationKey) {
		try {
			this.conn.copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
			LOGGER.debug(LOGDISPLAY, SUCCESS_UPLOAD_MESSAGE);
		} catch (AmazonS3Exception e) {
			LOGGER.error(LOGDISPLAY, e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			ExceptionHandler.exceptionHandler(e);
		} catch (SdkClientException e) {
			ExceptionHandler.exceptionHandler(e);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter#getPacket(java.lang.String)
	 */
	/*
	 * This method fetches the packet corresponding to an enrolment ID and returns
	 * it
	 *
	 * @Param enrolmentId
	 * 
	 * @see com.demo.server.filehandler.FileHandler#getPacket(java.lang.String)
	 */
	@Override
	public InputStream getPacket(String enrolmentId) {
		S3Object object = null;
		try {
			object = this.conn.getObject(new GetObjectRequest(enrolmentId, enrolmentId));
			LOGGER.debug(LOGDISPLAY, enrolmentId, "fetched from DFS");
		} catch (AmazonS3Exception e) {
			LOGGER.error(LOGDISPLAY, e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			ExceptionHandler.exceptionHandler(e);
		} catch (SdkClientException e) {
			ExceptionHandler.exceptionHandler(e);
		}
		return object != null ? object.getObjectContent() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.demo.server.filehandler.FileHandler#getFile(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public InputStream getFile(String enrolmentId, String fileName) {
		S3Object object = null;
		try {
			object = this.conn.getObject(new GetObjectRequest(enrolmentId, fileName));
			LOGGER.debug(LOGDISPLAY, enrolmentId, fileName, "fetched from DFS");
		} catch (AmazonS3Exception e) {
			LOGGER.error(LOGDISPLAY, e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			ExceptionHandler.exceptionHandler(e);
		} catch (SdkClientException e) {
			ExceptionHandler.exceptionHandler(e);
		}
		return object != null ? object.getObjectContent() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.demo.server.filehandler.FileHandler#unpackPacket(java.lang.String)
	 */
	@Override
	public void unpackPacket(String enrolmentId) throws IOException {
		InputStream packetStream = getPacket(enrolmentId);
		ZipInputStream zis = new ZipInputStream(packetStream);
		byte[] buffer = new byte[2048];
		byte[] file;
		ZipEntry ze = zis.getNextEntry();
		while (ze != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int len;
			while ((len = zis.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			file = out.toByteArray();
			InputStream inputStream = new ByteArrayInputStream(file);
			String[] arr = ze.getName().split("/");
			String fileName = arr[arr.length - 1].split("\\.")[0];
			storeFile(enrolmentId, fileName.toUpperCase(), inputStream);
			inputStream.close();
			ze = zis.getNextEntry();
		}
		LOGGER.debug(LOGDISPLAY, enrolmentId, "unpacked successfully into DFS");
		zis.closeEntry();
		zis.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.demo.server.filehandler.FileHandler#deletePacket(java.lang.String)
	 */
	@Override
	public Boolean deletePacket(String enrolmentId) {
		try {
			this.conn.deleteObject(enrolmentId, enrolmentId);
			LOGGER.debug(LOGDISPLAY, enrolmentId, "deleted from DFS successfully");
		} catch (AmazonS3Exception e) {
			LOGGER.error(LOGDISPLAY, e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			ExceptionHandler.exceptionHandler(e);
		} catch (SdkClientException e) {
			ExceptionHandler.exceptionHandler(e);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.demo.server.filehandler.FileHandler#deleteFile(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public Boolean deleteFile(String enrolmentId, String fileName) {
		try {
			this.conn.deleteObject(enrolmentId, fileName);
			LOGGER.debug(LOGDISPLAY, enrolmentId, fileName, "deleted from DFS successfully");
		} catch (AmazonS3Exception e) {
			LOGGER.error(LOGDISPLAY, e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
			ExceptionHandler.exceptionHandler(e);
		} catch (SdkClientException e) {
			ExceptionHandler.exceptionHandler(e);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.filesystem.adapter.FileSystemAdapter#
	 * checkFileExistence(java.lang.String, java.lang.Object)
	 */
	@Override
	public Boolean checkFileExistence(String enrolmentId, String fileName) {
		boolean result = false;
		try {
			if (getFile(enrolmentId, fileName) != null) {
				result = true;
			}
		} catch (PacketNotFoundException e) {
			LOGGER.error(LOGDISPLAY, "INVALID_PACKET_FILE_NAME");
			return false;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter#isPacketPresent(java.lang.String)
	 */
	@Override
	public Boolean isPacketPresent(String registrationId) {
		return this.getPacket(registrationId) != null;
	}


}
