import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { MaterialModule } from 'src/app/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { FooterComponent } from '../footer/footer.component';
import { AuthService } from 'src/app/auth/auth.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let footerComponent: FooterComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HeaderComponent, FooterComponent ],
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
      RouterTestingModule,
      BrowserAnimationsModule
      ],
      providers: [
         AuthService
        ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should remove token', () => {
    component.removeToken();
    fixture.detectChanges();
    expect(localStorage.getItem('loggedIn')).toBe('false');
  });

  it('should do logout', () => {
    component.doLogout();
    fixture.detectChanges();
    expect(localStorage.getItem('loggedOut')).toBe('true');
  })
});
