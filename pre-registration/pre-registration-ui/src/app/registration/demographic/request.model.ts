import { DemoIdentityModel } from './demo.identity.model';

export class RequestModel {
  constructor(
    public preRegistrationId: string,
    public createdBy: string,
    public createdDateTime: string,
    public updatedBy: string,
    public updatedDateTime: string,
    public statusCode: string,
    public langCode: string,
    public demographicDetails: DemoIdentityModel
  ) {}
}
