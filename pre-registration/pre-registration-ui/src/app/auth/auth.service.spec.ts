import { TestBed, inject } from '@angular/core/testing';
import { AuthService } from './auth.service';

fdescribe('Auth service', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
          providers: [AuthService]
        });
      });

      it('should be created', inject([AuthService], (service: AuthService) => {
        expect(service).toBeTruthy();
      }));

      it('token should be created', inject([AuthService], (service: AuthService) => {
        service.setToken();
        expect(service.token).toBe('settingToken');
      }));

      it('token should be deleted', inject([AuthService], (service: AuthService) => {
        service.setToken();
        service.removeToken();
        expect(service.token).toBe(null);
      }));

      it('Authentication check true', inject([AuthService], (service: AuthService) => {
        service.setToken();
        expect(service.isAuthenticated()).toBeTruthy();
      }));

      it('Authentication check false', inject([AuthService], (service: AuthService) => {
        service.setToken();
        service.removeToken();
        expect(service.isAuthenticated()).toBeFalsy();
      }));
})