import { IdentityModel } from './identity.model';

export class UserModel {
  constructor(public preRegId: string, public identity: IdentityModel, public files: File[]) {}
}
