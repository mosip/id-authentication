import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import * as appConstants from '../../app.constants';

@Injectable()

export class DataStorageService {

  constructor(private http: HttpClient) { }

  private BASE_URL = appConstants.admin_base_url;
  private langCode = 'eng';

  getMasterDataCards() {
    return this.http.get(this.BASE_URL + 'mastercards/' + this.langCode);
  }

  getMasterData(appendURL: string) {
    const url = appConstants.masterData_base_url + appendURL;
    return this.http.get(url);
  }

}
