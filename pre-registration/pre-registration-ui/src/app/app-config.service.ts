import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AppConfigService {
  private appConfig: any;

  constructor(private http: HttpClient) {}

  loadAppConfig() {
    return this.http
      .get('./assets/config.json')
      .toPromise()
      .then(data => {
      console.log(data);
        this.appConfig = JSON.parse(data.toString());
      });
  }

  getConfig() {
    return this.appConfig;
  }
}
