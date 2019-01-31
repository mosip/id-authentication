# Registration-Processor Message Sender

FileText for templates can contain following parameters with $ prefix. These variables is being used as a placeholder 
whose values will be replaced as present in id json file. These variables has the same meaning as in identity object.

	 name
	 phoneNumber
	 emailID
	 dateOfBirth
	 age
	 gender
	 addressLine1
	 addressLine2
	 addressLine3
	 region
	 province
	 city
	 postalCode
	 parentOrGuardianName
	 parentOrGuardianRIDOrUIN
	 proofOfAddress
	 proofOfIdentity
	 proofOfRelationship
	 proofOfDateOfBirth
	 individualBiometrics
	 parentOrGuardianBiometrics
	 localAdministrativeAuthority
	 idSchemaVersion
	 cnieNumber
	 UIN
	 RID
	 
eg: FileText for UIN Generation SMS notification
"Hi $name,
	Your UIN for the Registration $RID has been successfully generated and will reach soon at your Postal Address."
	
Here $name will be replaced with actual name, and $RID will be replaced with actual registration id.


