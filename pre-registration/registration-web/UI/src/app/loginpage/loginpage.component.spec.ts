import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginpageComponent } from './loginpage.component';
import { MaterialModule } from '../material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppService } from '../app.service';
import { DataExchangeService } from '../data-exchange.service';
import { FormsModule } from '@angular/forms';
import { HttpModule, BaseRequestOptions, Http } from '@angular/http';
import { HeaderComponent } from '../header/header.component';
import { LanguageSelectionComponent } from '../language-selection/language-selection.component';
import { Router } from '@angular/router';
import { MockBackend } from '@angular/http/testing';

describe('LoginpageComponent', () => {
  let component: LoginpageComponent;
  let fixture: ComponentFixture<LoginpageComponent>;
  let service: AppService;
  let DataService:DataExchangeService;
  let backend: MockBackend;
  beforeEach(async(() => {
    const mockroute = {
        navigate: () => ({})
      }
    TestBed.configureTestingModule({
      imports: [MaterialModule, BrowserAnimationsModule,FormsModule,HttpModule ],
      declarations: [ LoginpageComponent,HeaderComponent,LanguageSelectionComponent ],
      providers: [{ provide: Router, useValue: mockroute },
        AppService,MockBackend,DataExchangeService,
        BaseRequestOptions,
        {
          provide: Http, 
          useFactory: (backend, options) => new Http(backend, options), 
          deps: [MockBackend, BaseRequestOptions] 
        }
      ]
    })
    .compileComponents();
    backend = TestBed.get(MockBackend); 
    service = TestBed.get(AppService); 
    DataService=TestBed.get(DataExchangeService);
  }));
  beforeEach(() => {
    fixture = TestBed.createComponent(LoginpageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


  it('should check mobilevalidation', () => {
    DataService.setLogin("9876543211","mobile");
    component.mobileValidation("9876543211");
  });

  it('should check emailvalidation', () => {
    DataService.setLogin("rajath@gmail.com","email");
    component.emailValidation("rajath@gmail.com");
  });

  it('should check for failed validation', async(() => {
    component.emailValidation("ra1233");
    component.mobileValidation("98765erwer3");
    component.generateOTP();
 }));


  it('should check generateOTP', async(() => {
    component.emailValidation("rajath@gmail.com");
    component.mobileValidation("9876543211");
    backend.connections.subscribe(connection => { 
    connection.mockRespond(true);
    service.generateOTP("9876543211");
    const mockroute: Router = fixture.debugElement.injector.get(Router);
    spyOn(mockroute, 'navigate');
    component.generateOTP();
   });
 }));

 it('should check wrong input', async(() => {
    component.emailValidation("456789");
    component.mobileValidation("9876etrtu");
    backend.connections.subscribe(connection => { 
    connection.mockRespond(false);
    service.generateOTP("98tqwr");
    const mockroute: Router = fixture.debugElement.injector.get(Router);
    spyOn(mockroute, 'navigate');
    component.generateOTP();
   });

 }));

});
