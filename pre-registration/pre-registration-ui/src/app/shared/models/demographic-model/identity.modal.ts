import { AttributeModel } from './attribute.modal';

export class IdentityModel {
  constructor(
    public fullName: AttributeModel[],
    public dateOfBirth: AttributeModel[],
    public gender: AttributeModel[],
    public addressLine1: AttributeModel[],
    public addressLine2: AttributeModel[],
    public addressLine3: AttributeModel[],
    public region: AttributeModel[],
    public province: AttributeModel[],
    public city: AttributeModel[],
    public localAdministrativeAuthority: AttributeModel[],
    public postalcode: AttributeModel[],
    public mobileNumber: AttributeModel[],
    public emailId: AttributeModel[],
    public CNEOrPINNumber: AttributeModel[]
  ) {}
}
