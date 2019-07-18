package io.mosip.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;

import io.mosip.dao.RegProcTransactionDb;
import io.mosip.service.RegistrationTransactionData;
import io.mosip.util.RegistrationProcessorRequests;

public class PacketUpload {
	RegistrationProcessorRequests requests=new RegistrationProcessorRequests();
	RegistrationTransactionData registartionTransactionData=new RegistrationTransactionData();
public boolean syncPacket(File file) {
	boolean syncStatus=requests.syncRequest(file);
	return syncStatus;
}
public boolean uploadPacket(File file) {
	boolean uploadStatus=false; 
	try {
		uploadStatus = requests.UploadPacket(file);
	} catch (ParseException | IOException e) {
		return false;
	}
	return uploadStatus;
}
public List<String> compareDbStatus(String regId) {
	RegProcTransactionDb transaction=new RegProcTransactionDb();
	List<String> dbBits=transaction.readStatus(regId);
	/*List<Integer> transactionList=registartionTransactionData.getList(stageBits);
	List<String> statusCodes=registartionTransactionData.setCharacter(transactionList);*/
	return dbBits;

	
}
public String getUin(String regId) {
	String uin=requests.getUin(regId);
	return uin;
}
/*public static void main(String[] args) {
	PacketUpload upload=new PacketUpload();
	File file=new File("src\\test\\resources\\Packets\\");
	File[] listOfFiles=file.listFiles();
	for(File packet:listOfFiles) {
		if(packet.getName().contains(".zip")) {
				boolean status=upload.syncPacket(packet);
				System.out.println(status);
				boolean uploadStatus=upload.uploadPacket(packet);
				System.out.println(uploadStatus);
				boolean dbStatus=upload.compareDbStatus(packet.getName().substring(0, packet.getName().lastIndexOf(".")), "11111111");
		}
	}
}*/
}
