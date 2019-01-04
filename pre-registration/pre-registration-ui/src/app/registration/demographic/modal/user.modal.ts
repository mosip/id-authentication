import { IdentityModel } from './identity.modal';
import { FileModel } from './file.model';
import { RequestModel } from './request.modal';
export class UserModel {
  constructor(public preRegId?: string, public request?: RequestModel, public files?: any[]) {}
}
