import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeHeaderComponent } from './home-header.component';
import { MaterialModule } from '../material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { LanguageSelectionComponent } from '../language-selection/language-selection.component';
import { Router } from '@angular/router';

describe('HomeHeaderComponent', () => {
  let component: HomeHeaderComponent;
  let fixture: ComponentFixture<HomeHeaderComponent>;
  beforeEach(async(() => {
    var store = {};
    
    spyOn(sessionStorage, 'getItem').and.callFake((key: string): string => {
      return store[key] || null;
    });
    spyOn(sessionStorage, 'removeItem').and.callFake((key: string): void => {
      delete store[key];
    });
    spyOn(sessionStorage, 'setItem').and.callFake((key: string, value: string): string => {
      return store[key] = <string>value;
    });
    spyOn(sessionStorage, 'clear').and.callFake(() => {
      store = {};
    });
    const mockroute = {
      navigate: () => ({})
    }
    TestBed.configureTestingModule({
      imports: [MaterialModule, BrowserAnimationsModule],
      declarations: [HomeHeaderComponent, LanguageSelectionComponent],
      providers: [{ provide: Router, useValue: mockroute }]
    })
    .compileComponents();
    fixture = TestBed.createComponent(HomeHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));
  
  
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get session', async(() => {
    sessionStorage.setItem('lan', 'en');
    component.ngOnInit();
  }));

  it('should check func logout', async(() => {
    sessionStorage.setItem('loginuser', 'en');
    const mockroute: Router = fixture.debugElement.injector.get(Router);
    spyOn(mockroute, 'navigate');
    component.logout();
  }));

});
