import { RequestModel } from './request.modal';
import { CodeValueModal } from './code.value.modal';

export class UserModel {
  constructor(
    public preRegId?: string,
    public request?: RequestModel,
    public files?: any[],
    public location?: CodeValueModal[]
  ) {}
}
