import { RequestModel } from './request.model';

export class UserModel {
  constructor(public preRegId: string, public request: RequestModel) {}
}
