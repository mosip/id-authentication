import { AttributeModel } from './attribute.modal';

/**
 * @description This is the data object for the request object for adding the user.
 * @author Shashank Agrawal
 *
 * @export
 * @class IdentityModel
 */
export class IdentityModel {
  constructor(
    public IDSchemaVersion: number,
    public fullName: AttributeModel[],
    public dateOfBirth: string,
    public gender: AttributeModel[],
    public addressLine1: AttributeModel[],
    public residenceStatus: AttributeModel[],
    public addressLine2: AttributeModel[],
    public addressLine3: AttributeModel[],
    public region: AttributeModel[],
    public province: AttributeModel[],
    public city: AttributeModel[],
    public localAdministrativeAuthority: AttributeModel[],
    public postalCode: string,
    public phone: string,
    public email: string,
    public CNIENumber: string
  ) {}
}
