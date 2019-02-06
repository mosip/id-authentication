import { AttributeModel } from './attribute.modal';

export class IdentityModel {
  constructor(
    public IDSchemaVersion: number,
    public fullName: AttributeModel[],
    public dateOfBirth: string,
    public gender: AttributeModel[],
    public addressLine1: AttributeModel[],
    public addressLine2: AttributeModel[],
    public addressLine3: AttributeModel[],
    public region: AttributeModel[],
    public province: AttributeModel[],
    public city: AttributeModel[],
    public localAdministrativeAuthority: AttributeModel[],
    public postalCode: string,
    public phone: string,
    public email: string,
    public CNIENumber: number
  ) {}
}
