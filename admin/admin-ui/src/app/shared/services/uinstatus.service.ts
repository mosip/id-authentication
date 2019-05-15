import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class UinstatusService {

  constructor(private http: HttpClient) { }

   getUinStatus(inputUin: String) {
    return this.http.get('http://localhost:8098/v1/admin/uinmgmt/status/' + inputUin.trim());
  }

}
