package io.mosip.kernel.tests;

import java.io.File;

public class TestCaseNameChanger {
	/**
	 * this method the change the testcase name for api
	 * 
	 * @param provide
	 *            the module and service name
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] arg) {
		String moduleName = "Kernel";
		String serviceName = "kernel/AdminSyncIncrementalData";
		String serviceName1 = "AdminSyncIncrementalData";
		
		// if parent service name is there please provide it
		//String parentServiceName = "NotificationServices";

		String path = "src" + File.separator + "test" + File.separator + "resources" + File.separator + serviceName;
		
		// if parent service is there use this as path
		//String path = "src" + File.separator + "test" + File.separator + "resources" + File.separator+ parentServiceName + File.separator + serviceName;
		
		File file = new File(path);
		File[] listOfFolders = file.listFiles();

		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				File oldName = new File(path + file.separator + listOfFolders[j].getName());
				File newName = new File(
						path + file.separator + moduleName + "_" + serviceName1 + "_" + listOfFolders[j].getName());
				boolean isFileRenamed = oldName.renameTo(newName);

				if (isFileRenamed)
					System.out.println("File has been renamed");
				else
					System.out.println("Error renaming the file");
			}

		}
	}
}
