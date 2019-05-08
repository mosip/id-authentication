import { TestBed } from '@angular/core/testing';
import { HttpHandler, HttpRequest } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { AuthInterceptorService } from './auth-interceptor.service';
describe('AuthInterceptorService', () => {
  let service: AuthInterceptorService;
  beforeEach(() => {
    const httpRequestStub = { clone: () => ({}) };
    const httpHandlerStub = { handle: () => ({}) };
    const authServiceStub = {};
    TestBed.configureTestingModule({
      providers: [
        AuthInterceptorService,
        { provide: HttpRequest, useValue: httpRequestStub },
        { provide: HttpHandler, useValue: httpHandlerStub },
        { provide: AuthService, useValue: authServiceStub }
      ]
    });
    service = TestBed.get(AuthInterceptorService);
  });
  it('can load instance', () => {
    expect(service).toBeTruthy();
  });
  describe('intercept', () => {
    it('makes expected calls', () => {
      const httpRequestStub = TestBed.get(HttpRequest);
      const httpHandlerStub: HttpHandler = TestBed.get(HttpHandler);
      spyOn(httpRequestStub, 'clone');
      spyOn(httpHandlerStub, 'handle');
      service.intercept(httpRequestStub, httpHandlerStub);
      expect(httpRequestStub.clone).toHaveBeenCalled();
      expect(httpHandlerStub.handle).toHaveBeenCalled();
    });
  });
});
