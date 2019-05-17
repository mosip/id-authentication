import Utils from '../../app.util';

export class RequestModel {
   requesttime = Utils.getCurrentDate();
   constructor(public id: string, public version: string, public request: any, public metadata: any) { }
}
