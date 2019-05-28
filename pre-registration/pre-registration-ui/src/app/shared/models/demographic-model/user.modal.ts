import { RequestModel } from './request.modal';
import { CodeValueModal } from './code.value.modal';
import { ResponseModel } from './response.model';
import { FilesModel } from './files.model';

export class UserModel {
  constructor(
    public preRegId?: string,
    public request?: ResponseModel,
    public files?: FilesModel,
    public location?: CodeValueModal[]
  ) {}
}
