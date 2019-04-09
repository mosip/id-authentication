import { RequestModel } from './request.modal';
import { CodeValueModal } from './code.value.modal';
import { ResponseModel } from './response.model';

export class UserModel {
  constructor(
    public preRegId?: string,
    // public request?: RequestModel,
    public request?: ResponseModel,
    public files?: any[],
    public location?: CodeValueModal[]
  ) {}
}
