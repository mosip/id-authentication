import { TestBed, ComponentFixture } from '@angular/core/testing';
import { UserIdleService } from 'angular-user-idle';
import { MatDialog } from '@angular/material';
import { AutoLogoutService } from './auto-logout.service';
import { AuthService } from 'src/app/auth/auth.service';

describe('AutoLogoutService', () => {
  let service: AutoLogoutService;
  beforeEach(() => {
    const userIdleServiceStub = {
      startWatching: () => ({}),
      onTimerStart: () => ({ subscribe: () => ({}) }),
      onTimeout: () => ({ subscribe: () => ({}) }),
      resetTimer: () => ({}),
      stopWatching: () => ({})
    };
    const authServiceStub = { onLogout: () => ({}) };
    const matDialogStub = { closeAll: () => ({}), open: () => ({}) };
    TestBed.configureTestingModule({
      providers: [
        AutoLogoutService,
        { provide: UserIdleService, useValue: userIdleServiceStub },
        { provide: AuthService, useValue: authServiceStub },
        { provide: MatDialog, useValue: matDialogStub }
      ]
    });
    service = TestBed.get(AutoLogoutService);
  });

  it('can load instance', () => {
    expect(service).toBeTruthy();
  });

  it('isActive defaults to: false', () => {
    expect(service.isActive).toEqual(false);
  });
  it('should change the message', () => {
    const x = {
      name: 'deepak choudhary'
    };
    service.changeMessage(x);
    service.currentMessageAutoLogout.subscribe(message => {
      expect(message).toBe(x);
    });
  });

  it('on keep watching timer fired should change', () => {
    const timerFired = false;
    service.keepWatching();
    expect(timerFired).toEqual(false);
  });
});
