import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { TranslateService, TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HeaderComponent } from './header.component';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient } from 'selenium-webdriver/http';
import { HttpClientModule } from '@angular/common/http';
import { DataStorageService } from '../services/data-storage.service';
import { MaterialModule } from 'src/app/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  beforeEach(() => {
    const dataServiceStub = { getSecondaryLanguageLabels: () => ({}) };
    const authServiceStub = { isAuthenticated: () => ({}), onLogout: () => ({}) };
    const routerStub = { navigate: () => ({}) };
    const translateServiceStub = { use: () => ({}) };
    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [HeaderComponent],
      imports: [
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient]
          }
        }),
        HttpClientModule,
        MaterialModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceStub },
        { provide: Router, useValue: routerStub },
        { provide: TranslateService, useValue: translateServiceStub },
        { provide: DataStorageService, useValue: dataServiceStub }
      ]
    });
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
  });
  it('can load instance', () => {
    expect(component).toBeTruthy();
  });
  describe('onLogoClick', () => {
    it('makes expected calls', () => {
      const routerStub: Router = fixture.debugElement.injector.get(Router);
      spyOn(routerStub, 'navigate');
      component.onLogoClick();
      expect(routerStub.navigate).toHaveBeenCalled();
    });
  });
  describe('onHome', () => {
    it('makes expected calls', () => {
      const routerStub: Router = fixture.debugElement.injector.get(Router);
      spyOn(routerStub, 'navigate');
      component.onHome();
      expect(routerStub.navigate).toHaveBeenCalled();
    });
  });
  // describe('doLogout', () => {
  //   it('makes expected calls', () => {
  //     const authServiceStub: AuthService = fixture.debugElement.injector.get(AuthService);
  //     spyOn(authServiceStub, 'onLogout');
  //     component.doLogout();
  //     expect(authServiceStub.onLogout).toHaveBeenCalled();
  //   });
  // });
});
