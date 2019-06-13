import { Injectable } from '@angular/core';
import { Location } from '@angular/common';
import { HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

import * as appConstants from '../app.constants';

/**
 * @description This is the interceptor service, which intercept all the http request.
 * @author Shashank Agrawal
 *
 * @export
 * @class AuthInterceptorService
 */
@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {
  /**
   * @description Creates an instance of AuthInterceptorService.
   * @memberof AuthInterceptorService
   */
  constructor(private router: Router, private locaiton: Location) {}

  /**
   * @description This is the interceptor, which intercept all the http request
   *
   * @param {HttpRequest<any>} req
   * @param {HttpHandler} next
   * @returns {Observable<HttpEvent<any>>}
   * @memberof AuthInterceptorService
   */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const copiedReq = req.clone({
      withCredentials: true
    });
    return next.handle(copiedReq).pipe(
      retry(2),
      catchError((error: HttpErrorResponse) => {
        if (error[appConstants.ERROR][appConstants.NESTED_ERROR] !== 'KER-ATH-401') {
          console.log('error interceptor', error);
          // 401 handled in auth.interceptor
          console.log('loc', this.locaiton);
          this.router.navigateByUrl('/');

          // this.autoLogout.onLogOut();
          // this.locaiton.reload();
        }
        return throwError(error);
      })
    );
  }
}
