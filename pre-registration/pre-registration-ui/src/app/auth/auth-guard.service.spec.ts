import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot } from '@angular/router';
import { RouterStateSnapshot } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { AuthGuardService } from './auth-guard.service';
describe('AuthGuardService', () => {
  let service: AuthGuardService;
  beforeEach(() => {
    const activatedRouteSnapshotStub = {};
    const routerStateSnapshotStub = {};
    const routerStub = { navigate: () => ({}) };
    const authServiceStub = { isAuthenticated: () => ({}) };
    TestBed.configureTestingModule({
      providers: [
        AuthGuardService,
        {
          provide: ActivatedRouteSnapshot,
          useValue: activatedRouteSnapshotStub
        },
        { provide: RouterStateSnapshot, useValue: routerStateSnapshotStub },
        { provide: Router, useValue: routerStub },
        { provide: AuthService, useValue: authServiceStub }
      ]
    });
    service = TestBed.get(AuthGuardService);
  });
  it('can load instance', () => {
    expect(service).toBeTruthy();
  });
  describe('canActivate', () => {
    it('makes expected calls', () => {
      const activatedRouteSnapshotStub: ActivatedRouteSnapshot = TestBed.get(
        ActivatedRouteSnapshot
      );
      const routerStateSnapshotStub: RouterStateSnapshot = TestBed.get(
        RouterStateSnapshot
      );
      const routerStub: Router = TestBed.get(Router);
      const authServiceStub: AuthService = TestBed.get(AuthService);
      spyOn(routerStub, 'navigate');
      spyOn(authServiceStub, 'isAuthenticated');
      service.canActivate(activatedRouteSnapshotStub, routerStateSnapshotStub);
      expect(routerStub.navigate).toHaveBeenCalled();
      expect(authServiceStub.isAuthenticated).toHaveBeenCalled();
    });
  });
});
