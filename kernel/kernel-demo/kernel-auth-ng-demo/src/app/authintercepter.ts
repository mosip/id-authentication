import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})

export class AuthIntercepter implements HttpInterceptor {
  constructor() { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        request = request.clone({
        withCredentials : true
      });

    return next.handle(request).pipe(tap(event => {
      if (event instanceof HttpResponse) {
        console.log('event of type response', event);
      }
    }, err => {
      console.log(err);
    }));
  }
}
