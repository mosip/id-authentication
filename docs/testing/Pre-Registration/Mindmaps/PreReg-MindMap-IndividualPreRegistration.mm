<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1542260148795" ID="ID_171845682" MODIFIED="1542263385544" TEXT="Individual Registration">
<node COLOR="#339900" CREATED="1550742074285" ID="ID_1003172892" MODIFIED="1550745111994" POSITION="right" TEXT="Individual selects a language">
<node COLOR="#339900" CREATED="1550742125055" ID="ID_85316719" MODIFIED="1550745119747" TEXT="Login with Mobile num/Email ID">
<node COLOR="#339900" CREATED="1550742171130" ID="ID_914123403" MODIFIED="1550748428554" TEXT="Provide consent to process and store individual data">
<arrowlink DESTINATION="ID_592334386" ENDARROW="Default" ENDINCLINATION="238;0;" ID="Arrow_ID_1140075936" STARTARROW="None" STARTINCLINATION="238;0;"/>
<linktarget COLOR="#b0b0b0" DESTINATION="ID_914123403" ENDARROW="Default" ENDINCLINATION="2392;0;" ID="Arrow_ID_518253219" SOURCE="ID_642364502" STARTARROW="None" STARTINCLINATION="2392;0;"/>
<linktarget COLOR="#b0b0b0" DESTINATION="ID_914123403" ENDARROW="Default" ENDINCLINATION="3692;0;" ID="Arrow_ID_981832963" SOURCE="ID_649879626" STARTARROW="None" STARTINCLINATION="3692;0;"/>
</node>
</node>
</node>
<node COLOR="#339900" CREATED="1542263466956" HGAP="18" ID="ID_592334386" MODIFIED="1550745273980" POSITION="right" TEXT="Individual to provide basic demographic information -&gt; PRID should be generated" VSHIFT="9">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_592334386" ENDARROW="Default" ENDINCLINATION="238;0;" ID="Arrow_ID_1140075936" SOURCE="ID_914123403" STARTARROW="None" STARTINCLINATION="238;0;"/>
<node COLOR="#339900" CREATED="1550744753455" ID="ID_1320239857" MODIFIED="1550744981364" TEXT="user to Fill Demo Details (Full Name, Age/DOB, Gender, Address Lines(1,2,3), Region, Province, City, Local Administrative Authority, Mobile Number, Email ID, CNIE/PIN Number,Postal Code) and Upload Documents (POA, POB,POI, supporting document).">
<edge COLOR="#33cc00"/>
<node COLOR="#339900" CREATED="1542265789487" HGAP="23" ID="ID_1168740351" MODIFIED="1550742705946" TEXT="Individual proceeds to upload documents page. System should display document categories as per applicant type" VSHIFT="14">
<node COLOR="#339900" CREATED="1542265912690" ID="ID_1523891036" MODIFIED="1550742735958" TEXT="&#xa0;For each category, system should enable display of the list of valid document types">
<node COLOR="#339900" CREATED="1542266022395" ID="ID_121870457" MODIFIED="1550742885466" TEXT="System should allow to view or modify uploaded file">
<node COLOR="#339900" CREATED="1542263989944" ID="ID_326484546" MODIFIED="1542336720761" TEXT="System will perform size check after Document upload and revert the user to upload again if the Document Size is more than 1MB (Configurable).">
<node COLOR="#339900" CREATED="1542263619601" ID="ID_563963449" MODIFIED="1542336720761" TEXT="user not allowed to upload more than one document per category.">
<node COLOR="#339900" CREATED="1542265637907" ID="ID_1784307226" MODIFIED="1550748370633" TEXT="system should provide preview page with all the Filled Demographic Details, Documents uploads for each Applicant">
<node BACKGROUND_COLOR="#ffffff" COLOR="#339900" CREATED="1542263529412" HGAP="15" ID="ID_973059360" MODIFIED="1550744962606" TEXT="Individual can Proceed to book appointment" VSHIFT="-6">
<edge COLOR="#666666"/>
</node>
<node COLOR="#339900" CREATED="1550748382015" ID="ID_649879626" MODIFIED="1550748428554" TEXT="Individual clicks on Add Applicant">
<arrowlink DESTINATION="ID_914123403" ENDARROW="Default" ENDINCLINATION="3692;0;" ID="Arrow_ID_981832963" STARTARROW="None" STARTINCLINATION="3692;0;"/>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542266391722" ID="ID_851463149" MODIFIED="1542336745286" TEXT="Document size">
<node COLOR="#cc0000" CREATED="1542266348202" HGAP="21" ID="ID_1591436448" MODIFIED="1542336745286" TEXT="Document exceeding permitted size with error code : PRG-PAM&#x200c;-001" VSHIFT="7"/>
</node>
<node COLOR="#cc0000" CREATED="1542266772022" ID="ID_1110897458" MODIFIED="1542336745285" TEXT="Virus scan">
<node COLOR="#cc0000" CREATED="1542266779888" ID="ID_1054825153" MODIFIED="1542336745285" TEXT="virus Scan Failed  with error code : PRG-PAM&#x200c;-005"/>
</node>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542266461020" ID="ID_451607887" MODIFIED="1542336773350" TEXT="Support Document ">
<node COLOR="#cc0000" CREATED="1542266452394" HGAP="19" ID="ID_1237208389" MODIFIED="1542336773349" TEXT="Support Document not uploaded with error code : PRG-PAM&#x200c;-002" VSHIFT="10"/>
</node>
<node COLOR="#cc0000" CREATED="1542266670134" ID="ID_1190888095" MODIFIED="1550742937125" TEXT="File type other than pdf">
<node COLOR="#cc0000" CREATED="1542266679670" ID="ID_1913154303" MODIFIED="1542336773349" TEXT="File Type not supported with error code : PRG-PAM&#x200c;-004"/>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542266522310" ID="ID_532345800" MODIFIED="1550744930640" TEXT="Blacklisted words">
<node COLOR="#cc0000" CREATED="1542266540262" ID="ID_32398527" MODIFIED="1542336773350" TEXT="Blacklisted Word entered  with error code : PRG-PAM&#x200c;-003" VSHIFT="10"/>
</node>
</node>
</node>
<node COLOR="#339900" CREATED="1550744013251" ID="ID_1320468813" MODIFIED="1550748333176" POSITION="left" TEXT="Individual clicks on Your application">
<node COLOR="#339900" CREATED="1542273232930" ID="ID_562982599" MODIFIED="1542336201455" TEXT="Individual navigates to the landing page of the Pre-registration portal">
<node COLOR="#339900" CREATED="1542268350952" ID="ID_196791281" MODIFIED="1550744108313" TEXT="Individual can view pre-registration application created for that user ID" VSHIFT="31">
<node COLOR="#339900" CREATED="1542273398512" HGAP="19" ID="ID_642364502" MODIFIED="1550744297194" TEXT="Individual can chose to create new application by clicking on create New Application" VSHIFT="20">
<arrowlink DESTINATION="ID_914123403" ENDARROW="Default" ENDINCLINATION="2392;0;" ID="Arrow_ID_518253219" STARTARROW="None" STARTINCLINATION="2392;0;"/>
</node>
<node COLOR="#339900" CREATED="1542273435530" ID="ID_550611055" MODIFIED="1542336201451" TEXT="Individual can chose to Modify data(Demo Details/Documents) by selecting the Application ID and clicking on Modify data">
<node COLOR="#339900" CREATED="1542273465541" HGAP="24" ID="ID_1878976364" MODIFIED="1542336604797" TEXT="Individual can chose to Modify Appointment by selecting the Application ID/IDs and clicking on Modify Appointment" VSHIFT="10">
<node COLOR="#339900" CREATED="1542273492958" ID="ID_1100303045" MODIFIED="1542336201449" TEXT="Individual can Delete Application or can cancel Appointment by clicking on the delete icon.">
<node COLOR="#339900" CREATED="1542273553935" HGAP="17" ID="ID_476440906" MODIFIED="1542336201448" TEXT="Individual can chose to view an Application" VSHIFT="13">
<node COLOR="#339900" CREATED="1542273637276" ID="ID_1178998923" MODIFIED="1542336201444" TEXT="Individual can chose to download the acknowledgement Receipt for Application in Booked Status by clicking on the download icon.">
<node COLOR="#339900" CREATED="1542273656027" ID="ID_1751977439" MODIFIED="1542336201444" TEXT="Individual can chose to select About US, FAQ, Contact navigational Links by clicking on Links in the header">
<node COLOR="#339900" CREATED="1542273676067" HGAP="14" ID="ID_1626145943" MODIFIED="1542336201444" TEXT="Individual can chose to Logout by clicking on the Logout link in the Header" VSHIFT="14">
<node COLOR="#339900" CREATED="1542273690400" ID="ID_52183078" MODIFIED="1542336201444" TEXT="Individual can chose to redirect to home/Landing screen on clicking the Home icon/Logo" VSHIFT="11">
<node COLOR="#339900" CREATED="1542275960210" ID="ID_843707750" MODIFIED="1542336201443" TEXT="system should audit all the transactions"/>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542336538560" ID="ID_358488466" MODIFIED="1542336773348" TEXT="Individual unable to select Application ID ">
<node COLOR="#cc0000" CREATED="1542336629354" ID="ID_133556372" MODIFIED="1542336773348" TEXT="System throws an error message" VSHIFT="9"/>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
