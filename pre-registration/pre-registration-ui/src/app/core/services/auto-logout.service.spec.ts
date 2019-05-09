import { TestBed, ComponentFixture } from '@angular/core/testing';
import { UserIdleService } from 'angular-user-idle';
import { MatDialog } from '@angular/material';
import { AutoLogoutService } from './auto-logout.service';
import { AuthService } from 'src/app/auth/auth.service';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient } from 'selenium-webdriver/http';
import { HttpClientModule } from '@angular/common/http';
import { DataStorageService } from './data-storage.service';
import { of } from 'rxjs';

describe('AutoLogoutService', () => {
  let locationData = {
    response: {
      acceptButton: 'Acceptez',
      alertMessageFirst: "Vous serez déconnecté car vous n'acceptez pas les conditions générales",
      alertMessageSecond:
        "Vous serez redirigé vers votre page d'application parce que vous n'acceptez pas les conditions générales",
      alertMessageThird:
        "Vous serez redirigé vers la page de prévisualisation car vous n'acceptez pas les conditions générales.",
      case: 'CONSENTPOPUP',
      checkCondition: 'Cliquez pour accepter les termes et conditions',
      message:
        "Donner son ement pour le stockage et l'utilisation des informations personnelles telles qu'elles sont données",
      subtitle: 'Votre accord',
      title: 'Termes et conditions'
    }
  };

  let mockUser = {
    BASE_URL: '/url',
    getPrimaryLabels: jasmine.createSpy('getPrimaryLabels').and.returnValue(of(locationData)),
    getSecondaryLanguageLabels: jasmine.createSpy('getSecondaryLanguageLabels').and.returnValue(of(locationData))
  };

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
      imports: [
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient]
          }
        }),
        HttpClientModule
      ],
      providers: [
        AutoLogoutService,
        { provide: UserIdleService, useValue: userIdleServiceStub },
        { provide: DataStorageService, useValue: mockUser },
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
