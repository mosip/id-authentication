import {
  HttpRequest,
  HttpHandler,
  HttpInterceptor,
  HttpEvent
} from "@angular/common/http";
import { Observable } from "rxjs/Observable";

export class AuthInterceptor implements HttpInterceptor {
  constructor() {}
  ngOnInit(): void {}
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    request = request.clone({
      withCredentials: true
    });

    return next.handle(request);
  }
}
