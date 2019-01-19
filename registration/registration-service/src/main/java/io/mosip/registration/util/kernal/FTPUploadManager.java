package io.mosip.registration.util.kernal;

import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_FILE_NOT_FOUND_ERROR_CODE;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_FTP_CONNECTION_ERROR_CODE;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_FTP_PROPERTIES_SET_ERROR_CODE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import io.mosip.registration.dto.PacketUploadDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

@Configuration
@PropertySource("classpath:application.properties")

@Service
public class FTPUploadManager {

	@Value("${FTP_SERVER_IP}")
	String SFTPHOST;

	@Value("${FTP_SERVER_PORT}")
	int SFTPPORT;

	@Value("${FTP_USER}")
	String SFTPUSER;

	@Value("${FTP_KEY_FILE_NAME}")
	String KEY_FILE;

	@Value("${FTP_SERVER_FILE_PATH}")
	String filePath;

	public void pushPacket(List<File> verifiedPackets, String destFolder, PacketUploadDTO packetUploadDto)
			throws RegBaseCheckedException {

		Channel channel = null;
		ChannelSftp sftpChannel = null;
		Session ftpSession = setFtpConnection(packetUploadDto);
		try {
			ftpSession.connect();
			channel = ftpSession.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(filePath.trim());

			try {
				sftpChannel.cd(destFolder.trim());
			} catch (SftpException e) {
				sftpChannel.mkdir(destFolder.trim());
				sftpChannel.cd(destFolder.trim());
			}
			for (File packet : verifiedPackets) {
				sftpChannel.put(new FileInputStream(packet), packet.getName());
			}
		} catch (JSchException e1) {

			throw new RegBaseCheckedException(REG_FTP_CONNECTION_ERROR_CODE.getErrorCode(),
					REG_FTP_CONNECTION_ERROR_CODE.getErrorMessage());
		} catch (FileNotFoundException e) {
			throw new RegBaseCheckedException(REG_FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_FILE_NOT_FOUND_ERROR_CODE.getErrorMessage());
		} catch (SftpException e) {
			throw new RegBaseCheckedException(REG_FTP_CONNECTION_ERROR_CODE.getErrorCode(),
					REG_FTP_CONNECTION_ERROR_CODE.getErrorMessage());
		} finally {
			if (ftpSession != null) {
				ftpSession.disconnect();
			}
			if (channel != null) {
				channel.disconnect();
			}
		}
	}

	public Session setFtpConnection(PacketUploadDTO packetUploadDto) throws RegBaseCheckedException {

		Session ftpSession = null;
		JSch jsch = new JSch();
		try {
			// jsch.addIdentity(getClass().getClassLoader().getResource(KEY_FILE).getPath());
			ftpSession = jsch.getSession(packetUploadDto.getUserid().trim(), SFTPHOST, SFTPPORT);
			ftpSession.setPassword(packetUploadDto.getPassword().trim());
			java.util.Properties config = new java.util.Properties();
			config.setProperty("StrictHostKeyChecking", "no");
			ftpSession.setConfig(config);
		} catch (JSchException e) {
			throw new RegBaseCheckedException(REG_FTP_PROPERTIES_SET_ERROR_CODE.getErrorCode(),
					REG_FTP_PROPERTIES_SET_ERROR_CODE.getErrorMessage());
		}
		return ftpSession;
	}

}
