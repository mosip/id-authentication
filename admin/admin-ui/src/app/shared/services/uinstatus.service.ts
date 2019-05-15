import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class UinstatusService {

  constructor(private http: HttpClient) { }

   getUinStatus(inputUin: String) {
    return this.http.get('https://dev.mosip.io/v1/admin/uinmgmt/status/' + inputUin.trim());
  }

}
