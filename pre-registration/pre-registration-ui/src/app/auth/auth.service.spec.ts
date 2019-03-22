import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { DataStorageService } from '../core/services/data-storage.service';
import { AuthService } from './auth.service';
describe('AuthService', () => {
  let service: AuthService;
  beforeEach(() => {
    const routerStub = { navigate: () => ({}) };
    const dataStorageServiceStub = {
      onLogout: () => ({ subscribe: () => ({}) })
    };
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: Router, useValue: routerStub },
        { provide: DataStorageService, useValue: dataStorageServiceStub }
      ]
    });
    service = TestBed.get(AuthService);
  });
  it('can load instance', () => {
    expect(service).toBeTruthy();
  });
  describe('onLogout', () => {
    it('makes expected calls', () => {
      const routerStub: Router = TestBed.get(Router);
      const dataStorageServiceStub: DataStorageService = TestBed.get(
        DataStorageService
      );
      spyOn(component, 'removeToken');
      spyOn(routerStub, 'navigate');
      spyOn(dataStorageServiceStub, 'onLogout');
      service.onLogout();
      expect(service.removeToken).toHaveBeenCalled();
      expect(routerStub.navigate).toHaveBeenCalled();
      expect(dataStorageServiceStub.onLogout).toHaveBeenCalled();
    });
  });
});
