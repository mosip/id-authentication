import { RequestModel } from './request.modal';
import { LocationModal } from './location.modal';

export class UserModel {
  constructor(
    public preRegId?: string,
    public request?: RequestModel,
    public files?: any[],
    public location?: LocationModal[]
  ) {}
}
