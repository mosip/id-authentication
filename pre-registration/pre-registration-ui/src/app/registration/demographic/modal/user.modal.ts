import { RequestModel } from './request.modal';
export class UserModel {
  constructor(public preRegId?: string, public request?: RequestModel, public files?: any[]) {}
}
