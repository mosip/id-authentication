import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import * as constants from './Constants';
import {environment} from '../environments/environment';
import { Params } from '@angular/router';


@Injectable()
export class AppService {
    Headers: Headers;
    Params : Params;
    options: RequestOptions;
    jsonBody: string;
    resource:string=environment.url;

    constructor(private http: Http) { }

    generateOTP(value) {
        this.Headers = new Headers();
        this.Headers.append('Content-Type', 'application/json');
        this.options = new RequestOptions({ headers: this.Headers});
          return this.http.get(this.resource+constants.LOGIN_URL+'?userName='+value,this.options)
    }

    validateOTP(username,otp) {
        this.jsonBody = JSON.stringify({ userInput: otp })

        this.Headers = new Headers();
        this.Headers.append('Content-Type', 'application/json');
        this.Params = new URLSearchParams();
        this.Params.append('userName', username); 
        this.options = new RequestOptions({ headers: this.Headers});
          return this.http.post(this.resource+constants.LOGIN_URL+'?'+this.Params,this.jsonBody,this.options)
    
    }

    update(value,body) {
        this.Headers = new Headers();
        this.Headers.append('Content-Type', 'application/json');
        this.Params = new URLSearchParams();
        this.Params.append('userName', value); 
        this.options = new RequestOptions({ headers: this.Headers});
          return this.http.put(this.resource+constants.UPDATE_URL+'?'+this.Params,body,this.options)
    
    }
  
}
