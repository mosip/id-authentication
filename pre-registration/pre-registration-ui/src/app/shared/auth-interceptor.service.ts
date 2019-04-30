import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

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
export class AuthInterceptorService {
  /**
   * @description Creates an instance of AuthInterceptorService.
   * @memberof AuthInterceptorService
   */
  constructor() {}

  /**
   * @description This is the interceptor, which intercept all the http request
   *
   * @param {HttpRequest<any>} req
   * @param {HttpHandler} next
   * @returns {Observable<HttpEvent<any>>}
   * @memberof AuthInterceptorService
   */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('Intercepted!', req);
    const copiedReq = req.clone({
      withCredentials: true
    });
    return next.handle(copiedReq);
  }
}
