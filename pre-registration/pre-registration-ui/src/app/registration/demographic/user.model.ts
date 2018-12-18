import { IdentityModel } from './identity.model';
import { FileModel } from './file.model';
export class UserModel {
  constructor(public preRegId: string, public identity: IdentityModel, public files: FileModel[]) {}
}
