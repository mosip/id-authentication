from pydantic import BaseModel
from typing import List

class MOSIPRequestedAuth(BaseModel):
    demo : bool = False
    pin : bool = False
    otp : bool = False
    bio : bool = False

class DemographicLanguageField(BaseModel):
    language: str
    value: str

class DemographicsModel(BaseModel):
    name : List[DemographicLanguageField]
    gender : List[DemographicLanguageField]
    #dob : str
    phoneNumber : str
    emailId : str
    # age : int
    fullAddress : List[DemographicLanguageField]

class KeyBindedTokenModel(BaseModel):
    type: str
    token: str
    format: str

class MOSIPEncryptAuthRequest(BaseModel):
    timestamp : str
    demographics : dict
    #biometrics : list
    #claimsMetadataRequired: bool
    #keyBindedTokens: list
    #password : str
    #otp: str

class MOSIPEncryptAuthRequestKeyBind(BaseModel):
    timestamp : str
    demographics : dict
    biometrics : list
    identityKeyBinding: dict

class MOSIPAuthRequest(BaseModel):
    id : str
    version : str
    individualId: str
    #individualIdType: str
    transactionID: str
    requestTime: str
    specVersion: str
    thumbprint: str
    domainUri: str
    env: str
    requestedAuth : MOSIPRequestedAuth = MOSIPRequestedAuth()
    consentObtained: bool
    requestHMAC: str
    requestSessionKey: str
    request: str
    metadata: dict
    identityKeyBinding: dict
    claimsMetadataRequired: bool
                
class CredentialDefRequest(BaseModel):
    context : list
    type : list
    credentialSubject : dict
