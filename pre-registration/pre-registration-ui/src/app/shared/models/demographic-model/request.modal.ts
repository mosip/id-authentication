import { DemoIdentityModel } from './demo.identity.modal';

export interface RequestModel {
  preRegistrationId: string;
  createdBy: string;
  createdDateTime: string;
  updatedBy: string;
  updatedDateTime: string;
  langCode: string;
  demographicDetails: DemoIdentityModel;
}
