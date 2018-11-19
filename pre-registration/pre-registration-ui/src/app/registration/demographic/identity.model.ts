import { AttributeModel } from './attribute.model';

export class IdentityModel {
  constructor(
    public FullName: AttributeModel[],
    public dateOfBirth: AttributeModel[],
    public gender: AttributeModel[],
    public addressLine1: AttributeModel[],
    public addressLine2: AttributeModel[],
    public addressLine3: AttributeModel[],
    public region: AttributeModel[],
    public province: AttributeModel[],
    public city: AttributeModel[],
    public postalcode: AttributeModel[],
    public localAdministrativeAuthority: AttributeModel[],
    public emailId: AttributeModel[],
    public mobileNumber: AttributeModel[],
    public CNEOrPINNumber: AttributeModel[],
    public age: AttributeModel[]
  ) {}
}
