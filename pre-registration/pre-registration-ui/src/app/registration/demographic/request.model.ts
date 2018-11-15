import { IdentityModel } from './identity.model';

export class RequestModel {
  constructor(
    public preRegistrationId: string,
    public createdBy: string,
    public updatedBy: string,
    public updatedDateTime: string,
    public statusCode: string,
    public langCode: string,
    public identity: IdentityModel
  ) {}
}
