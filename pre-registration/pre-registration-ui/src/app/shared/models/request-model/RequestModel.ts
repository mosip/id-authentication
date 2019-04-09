import Utils from 'src/app/app.util';
import * as appConstanst from '../../../app.constants';

export class RequestModel {
  version = appConstanst.VERSION;
  requesttime = Utils.getCurrentDate();
  constructor(private id: string, private request: any, private metadata?: any) {}
}
