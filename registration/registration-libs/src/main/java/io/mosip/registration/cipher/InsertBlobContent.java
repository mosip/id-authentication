package io.mosip.registration.cipher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class InsertBlobContent {
	
	static String ackContent = "<html><head><style>body{font-family:'Roboto';}table, .head{font-size:13px;}.previewPhoto{float:right;position:absolute;top:220px;}.photo{float:right;position:absolute;top:320px;}.applicantPhoto{position:absolute;top:-35px;left:670px;}.applicantPhotoLabel{color:#808080;font-size:10px;position:absolute;white-space:nowrap;top:-70px;left:690px;}.form{display:inline-block;}.section{align:center;padding:30px;width:800px;margin:auto;}.headings{color:#808080;font-size:10px;}td,p{display:inline;}.dataTable{word-wrap:break-word;table-layout:fixed;width:70%;}.headerTable{width:80%;}.uinHeaderTable{width:100%;}h5{display:inline;position:absolute;}.iris{top:-15px;left:158px;}.irisWithoutException{top:-15px;left:120px;}.tableWithoutException{width:50%;text-align:center;table-layout:fixed;margin:0 auto;}.biometricsTable{width:100%;text-align:center;table-layout:fixed;}.biometrics{position:relative;}.leftLittle{top:15px;left:98px;}.leftRing{top:3px;left:111px;}.leftMiddle{top:-7px;left:127px;}.leftIndex{top:3px;left:140px;}.rightIndex{top:3px;left:113px;}.rightMiddle{top:-7px;left:127px;}.rightRing{top:3px;left:140px;}.rightLittle{top:15px;left:154px;}.leftThumb{top:-4px;left:113px;}.rightThumb{top:-4px;left:138px;}li span{color:black;font-size:12px;}li{color:lightgrey}button{float:right;font-size:12px;border:none;background-color:transparent;outline:none;}button:active{background-color:black;color:white;}.modify{float:right;}</style>"
			+ "</head><body><div class='section'><div class='form'><table ${AckReceipt} class='${headerTable}'><tr><td><img src='${QRCodeSource}' border='0' width='145' height='145'/></td><td><p class='headings'>${RIDUserLangLabel} / ${RIDLocalLangLabel}</p><br/>${RID}</td><td ${UINUpdate}><p class='headings'>${UINUserLangLabel} / ${UINLocalLangLabel}</p><br/>${UIN}</td><td><p class='headings'>${DateUserLangLabel} / ${DateLocalLangLabel}</p><br/>${Date}</td></tr></table><table ${Preview} class='headerTable'><tr><td><p class='headings'>${PreRegIDUserLangLabel} / ${PreRegIDLocalLangLabel}</p><br/>${PreRegID}</td><td><p class='headings'>${DateUserLangLabel} / ${DateLocalLangLabel}</p><br/>${Date}</td></tr></table><br/><hr/><p class='head'><b>${DemographicInfo}</b></p><div ${Preview} class='modify'><img src='${ModifyImageSource}' border='0' width='15' height='15'/><button onclick='registration.modifyDemographicInfo()'>${Modify}</button></div><hr/><table class='dataTable'><tr><td><p class='headings'>${FullNameUserLangLabel} / ${FullNameLocalLangLabel}</p><br/>${FullName}<br/>${FullNameLocalLang}</td><td><p class='headings'>${GenderUserLangLabel} / ${GenderLocalLangLabel}</p><br/>${Gender}<br/>${GenderLocalLang}</td></tr><tr><td><p class='headings'>${DOBUserLangLabel} / ${DOBLocalLangLabel}</p><br/>${DOB}</td><td><p class='headings'>${AgeUserLangLabel} / ${AgeLocalLangLabel}</p><br/>${Age} ${YearsUserLang} / ${YearsLocalLang}</td></tr><tr><td><p class='headings'>${ForiegnerUserLangLabel} / ${ForiegnerLocalLangLabel}</p><br/>${ResidenceStatus}<br/>${ResidenceStatusLocalLang}</td><td><p class='headings'>${AddressLine1UserLangLabel} / ${AddressLine1LocalLangLabel}</p><br/>${AddressLine1}<br/>${AddressLine1LocalLang}</td></tr><tr><td><p class='headings'>${AddressLine2UserLangLabel} / ${AddressLine2LocalLangLabel}</p><br/>${AddressLine2}<br/>${AddressLine2LocalLang}</td><td><p class='headings'>${RegionUserLangLabel} / ${RegionLocalLangLabel}</p><br/>${Region}<br/>${RegionLocalLang}</td></tr><tr><td><p class='headings'>${ProvinceUserLangLabel} / ${ProvinceLocalLangLabel}</p><br/>${Province}<br/>${ProvinceLocalLang}</td><td><p class='headings'>${LocalAuthorityUserLangLabel} / ${LocalAuthorityLocalLangLabel}</p><br/>${LocalAuthority}<br/>${LocalAuthorityLocalLang}</td></tr><tr><td><p class='headings'>${MobileUserLangLabel} / ${MobileLocalLangLabel}</p><br/>${Mobile}</td><td><p class='headings'>${PostalCodeUserLangLabel} / ${PostalCodeLocalLangLabel}</p><br/>${PostalCode}</td></tr><tr><td><p class='headings'>${EmailUserLangLabel} / ${EmailLocalLangLabel}</p><br/>${Email}</td><td><p class='headings'>${CNIEUserLangLabel} / ${CNIELocalLangLabel}</p><br/>${CNIE}</td></tr><tr ${WithParent}><td><p class='headings'>${ParentNameUserLangLabel} / ${ParentNameLocalLangLabel}</p><br/>${ParentName}<br/>${ParentNameLocalLang}</td></tr></table><br/><p class='head'><b>${DocumentsUserLangLabel}</b></p>\r\n"
			+ "<div ${DocumentsEnabled} ${Preview} class='modify'><img src='${ModifyImageSource}' border='0' width='15' height='15'/><button onclick='registration.modifyDocuments()'>${Modify}</button></div><hr/><table ${DocumentsEnabled}><tr><td><p class='headings'>${DocumentsUserLangLabel} / ${DocumentsLocalLangLabel}</p><br/>${Documents}<br/>${DocumentsLocalLang}</td></tr></table><br/><p ${BiometricsEnabled} class='head'><b>${BiometricsUserLangLabel}</b></p><div ${BiometricsEnabled} ${Preview} class='modify'><img src='${ModifyImageSource}' border='0' width='15' height='15'/><button onclick='registration.modifyBiometrics()'>${Modify}</button></div><hr/><table ${BiometricsEnabled}><tr><td><p class='headings'>${BiometricsUserLangLabel} / ${BiometricsLocalLangLabel}</p><br/>${Biometrics}<br/>${BiometricsLocalLang}</td></tr></table><table ${IrisEnabled} ${WithException} class='biometricsTable'><tr><td><p class='headings'>${ExceptionPhotoUserLangLabel} / ${ExceptionPhotoLocalLangLabel}</p></td><td><p class='headings'>${LeftEyeUserLangLabel} / ${LeftEyeLocalLangLabel}</p></td><td><p class='headings'>${RightEyeUserLangLabel} / ${RightEyeLocalLangLabel}</p></td></tr><tr ${AckReceipt}><td><img src='${ExceptionImageSource}' border='0' width='80' height='80'/></td><td><div class='biometrics'><h5 class='iris'>${LeftEye}</h5><img src='${EyeImageSource}' border='0' width='85' height='80'/></div></td><td><div class='biometrics'><h5 class='iris'>${RightEye}</h5><img src='${EyeImageSource}' border='0' width='85' height='80'/></div></td></tr><tr ${Preview}><td><img src='${ExceptionImageSource}' border='0' width='80' height='80'/></td><td><img src='${CapturedLeftEye}' border='0' width='85' height='80'/></td><td><img src='${CapturedRightEye}' border='0' width='85' height='80'/></td></tr></table><table ${IrisEnabled} class='tableWithoutException' ${WithoutException}><tr><td><p class='headings'>${LeftEyeUserLangLabel} / ${LeftEyeLocalLangLabel}</p></td><td><p class='headings'>${RightEyeUserLangLabel} / ${RightEyeLocalLangLabel}</p></td></tr><tr ${AckReceipt}><td><div class='biometrics'><h5 class='irisWithoutException'>${LeftEye}</h5><img src='${EyeImageSource}' border='0' width='85' height='80'/></div></td><td><div class='biometrics'><h5 class='irisWithoutException'>${RightEye}</h5><img src='${EyeImageSource}' border='0' width='85' height='80'/></div></td></tr><tr ${Preview}><td><img src='${CapturedLeftEye}' border='0' width='85' height='80'/></td><td><img src='${CapturedRightEye}' border='0' width='85' height='80'/></td></tr></table><table ${FingerprintsCaptured} class='biometricsTable'><tr><td><p class='headings'>${LeftPalmUserLangLabel} / ${LeftPalmLocalLangLabel}</p></td><td><p class='headings'>${RightPalmUserLangLabel} / ${RightPalmLocalLangLabel}</p></td><td><p class='headings'>${ThumbsUserLangLabel} / ${ThumbsLocalLangLabel}</p></td></tr><tr ${AckReceipt}><td><div class='biometrics'><h5 class='leftLittle'>${leftLittle}</h5><h5 class='leftRing'>${leftRing}</h5><h5 class='leftMiddle'>${leftMiddle}</h5><h5 class='leftIndex'>${leftIndex}</h5><img src='${LeftPalmImageSource}' border='0' width='85' height='80'/></div></td><td><div class='biometrics'><h5 class='rightIndex'>${rightIndex}</h5><h5 class='rightMiddle'>${rightMiddle}</h5><h5 class='rightRing'>${rightRing}</h5><h5 class='rightLittle'>${rightLittle}</h5><img src='${RightPalmImageSource}' border='0' width='85' height='80'/></div></td><td><div class='biometrics'><h5 class='leftThumb'>${leftThumb}</h5><h5 class='rightThumb'>${rightThumb}</h5><img src='${ThumbsImageSource}' border='0' width='85' height='80'/></div></td></tr><tr ${Preview}><td><img src='${CapturedLeftSlap}' border='0' width='85' height='80'/></td><td><img src='${CapturedRightSlap}' border='0' width='85' height='80'/></td><td><img src='${CapturedThumbs}' border='0' width='85' height='80'/></td></tr><tr><td><p ${MissingLeftFingers} class='headings'>${LeftSlapExceptionUserLang} / ${LeftSlapExceptionLocalLang}</p></td><td><p ${MissingRightFingers} class='headings'>${RightSlapExceptionUserLang} / ${RightSlapExceptionLocalLang}</p></td><td><p ${MissingThumbs} class='headings'>${ThumbsExceptionUserLang} / ${ThumbsExceptionLocalLang}</p></td></tr></table><hr/>"
			+ "<table class='dataTable'><tr><td ${ROImage}><img src='${ROImageSource}' border='0' width='80' height='80'/></td><td><p class='headings'>${RONameUserLangLabel} / ${RONameLocalLangLabel}</p><br/>${ROName}<br/>${RONameLocalLang}</td><td><p class='headings'>${RegCenterUserLangLabel} / ${RegCenterLocalLangLabel}</p><br/>${RegCenter}<br/>${RegCenterLocalLang}</td></tr></table><hr/><br/></div><div ${FaceCaptureEnabled} ${AckReceipt} class='photo'><p class='applicantPhotoLabel'>${PhotoUserLang} / ${PhotoLocalLang}</p><img class='applicantPhoto' src='${ApplicantImageSource}' border='0' width='100' height='100'/></div><div ${FaceCaptureEnabled} ${Preview} class='previewPhoto'><p class='applicantPhotoLabel'>${PhotoUserLang} / ${PhotoLocalLang}</p><img class='applicantPhoto' src='${ApplicantImageSource}' border='0' width='100' height='100'/></div><div ${AckReceipt}><p>${ImportantGuidelines}</p><ul><li><span>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim Ad minim veniam, quis nostrud exercitation. Ullamco laboris nisi ut aliquip exea commodo consequat. Duis aute irure dolor in Reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.Cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id.</span></li><br/><li><span>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et.</span></li><br/><li><span>Dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex.</span></li><br/><li><span>Commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim Ad minim veniam, quis nostrud exercitation. Ullamco laboris nisi ut aliquip exea commodo consequat. Duis aute irure dolor in Reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.Cupidatat non proident, sunt in culpa qui offici.</span></li><br/><li><span>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et.</span></li></ul></div></div></body></html>";

	static String notificationContent = "'Dear ${ResidentName},\r\n"
			+ "Thank you for registering with Digital Identity platform . Your registration id is '${RID}'. The demographic details are as follows:\r\n"
			+ "1.Date:${Date}\r\n" + "2.Full Name:${FullName}\r\n" + "3.Date of Birth:${DOB}\r\n"
			+ "4.Gender:${Gender}\r\n" + "5.Address Line 1:${AddressLine1}\r\n" + "6.Address Line 2:${AddressLine2}\r\n"
			+ "7.Address Line 3:${AddressLine3}\r\n" + "8.Region:${Region}\r\n" + "9.City:${City}\r\n"
			+ "10.State or Province:${State}\r\n" + "11.Postal Code:${PostalCode}\r\n" + "12.Mobile:${Mobile}\r\n"
			+ "13.Email:${Email}\r\n" + "\r\n"
			+ "If there are any corrections to be made to the above details, kindly contact the Registration centre within the next 4 days.";

	public static void main(String[] args) {
		insertAckContent();
		insertNotificationContent();
	}

	public static void insertAckContent() {
		try {
			Connection con;
			PreparedStatement pre;
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			con = DriverManager.getConnection("jdbc:derby:D:\\MOSIP_GIT_HUB\\registration"
					+ "\\registration-client\\reg;bootPassword=mosip12345", "", "");

			InputStream inputStream = new ByteArrayInputStream(ackContent.getBytes(Charset.forName("UTF-8")));

			pre = con.prepareStatement("update master.template set file_txt=? where id='T01'");
			pre.setBinaryStream(1, inputStream, (int) ackContent.length());
			int count = pre.executeUpdate();

			con.commit();

			if(count==1) {
				System.out.println("Acknowledgement Template Updated");
			}

			pre.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertNotificationContent() {
		try {
			Connection con;
			PreparedStatement pre;
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			con = DriverManager.getConnection("jdbc:derby:D:\\MOSIP_GIT_HUB\\registration"
					+ "\\registration-client\\reg;bootPassword=mosip12345", "", "");

			InputStream inputStream = new ByteArrayInputStream(notificationContent.getBytes(Charset.forName("UTF-8")));

			pre = con.prepareStatement("update master.template set file_txt=? where id='T02'");
			pre.setBinaryStream(1, inputStream, (int) notificationContent.length());
			int count = pre.executeUpdate();

			con.commit();
			
			if(count==1) {
				System.out.println("Notification Template Updated");
			}

			pre.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
