import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()

export class DataStorageService {

  constructor(private http: HttpClient) { }

  private BASE_URL = 'https://dev.mosip.io/v1/admin/';
  private langCode = 'eng';

  getMasterDataCards() {
    return this.http.get(this.BASE_URL + 'mastercards/' + this.langCode);
  }

  getMasterData(url: string) {
    return this.http.get(url);
  }

}
