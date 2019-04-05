import { DemoIdentityModel } from './demo.identity.modal';

export interface ResponseModel {
  preRegistrationId: string;
  createdBy: string;
  createdDateTime: string;
  updatedDateTime: string;
  langCode: string;
  demographicDetails: DemoIdentityModel;
  // updatedBy: string;
}
