import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OtpComponent } from './otp.component';
import { MaterialModule } from '../material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HeaderComponent } from '../header/header.component';
import { LanguageSelectionComponent } from '../language-selection/language-selection.component';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AppService } from '../app.service';
import { Http, ConnectionBackend, RequestOptions, HttpModule, XHRBackend, BaseRequestOptions } from '@angular/http';
import { MockBackend } from '@angular/http/testing';
import { DataExchangeService } from '../data-exchange.service';

describe('OtpComponent', () => {
  let component: OtpComponent;
  let fixture: ComponentFixture<OtpComponent>;
  let service: AppService;
  let DataService:DataExchangeService;
  let backend: MockBackend;
  beforeEach(async(() => {
    const mockroute = {
        navigate: () => ({})
      }
    const fakeActivatedRoute = {
        snapshot:()=> ({})
      }
    TestBed.configureTestingModule({
      imports: [MaterialModule, BrowserAnimationsModule,FormsModule,HttpModule ],
      declarations: [ OtpComponent,HeaderComponent,LanguageSelectionComponent ],
      providers: [{ provide: Router, useValue: mockroute },
        {provide: ActivatedRoute, useValue: fakeActivatedRoute},
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
    fixture = TestBed.createComponent(OtpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    DataService.setLogin("9876543211","mobile");
    DataService.getLogin();
    component.ngOnInit();
    expect(component).toBeTruthy();
  });

  it('should check validateOTP', async(() => {
        backend.connections.subscribe(connection => { 
        connection.mockRespond(true);
        service.validateOTP("9876543212","123");
        const mockroute: Router = fixture.debugElement.injector.get(Router);
        spyOn(mockroute, 'navigate');
        component.validateOTP();
       });
    
     }));
});
