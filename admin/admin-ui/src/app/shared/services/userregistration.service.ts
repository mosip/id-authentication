import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import * as appConstants from '../../app.constants';

@Injectable()
export class UserregistrationService {

  constructor(private http: HttpClient) { }

  getGenderTypes() {
    return this.http.get(`https://${appConstants.base_url}/v1/masterdata/gendertypes`);
  }
}
