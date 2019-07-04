import { RequestModel } from './request.modal';

export class DemographicModel {
  constructor(public id: string, public version: string, public requesttime: string, public request: RequestModel) {}
}
