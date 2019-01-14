import { AttributeModel } from './attribute.modal';

export class IdentityModel {
  constructor(
    public IDSchemaVersion: string,
    public fullName: AttributeModel[],
    public dateOfBirth: string, //
    public gender: AttributeModel[],
    public addressLine1: AttributeModel[],
    public addressLine2: AttributeModel[],
    public addressLine3: AttributeModel[],
    public region: AttributeModel[],
    public province: AttributeModel[],
    public city: AttributeModel[],
    public localAdministrativeAuthority: AttributeModel[],
    public postalcode: string, //
    public mobileNumber: string, //
    public emailId: string, //
    public CNEOrPINNumber: string //
  ) {}
}
