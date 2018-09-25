import { TestBed, inject, fakeAsync } from '@angular/core/testing';

import { AppService } from './app.service';
import { HttpModule, BaseRequestOptions, Http } from '@angular/http';
import { MockBackend } from '@angular/http/testing';

describe('AppService', () => {
    let service: AppService;
    let backend: MockBackend;
    let mockURLParam;
  
  beforeEach(() => {
     mockURLParam = {
        URLSearchParams: () => ({})
              }
    TestBed.configureTestingModule({
      imports: [HttpModule ],
      providers: [AppService,
        MockBackend,
        BaseRequestOptions,
        {
            provide: Http, 
            useFactory: (backend, options) => new Http(backend, options), 
            deps: [MockBackend, BaseRequestOptions] 
          }]
    });
    backend = TestBed.get(MockBackend); 

    service = TestBed.get(AppService); 
  });

  it('should be created', inject([AppService], (service: AppService) => {
    expect(service).toBeTruthy();
  }));

  it('should check generateOTP',fakeAsync(() => {
      mockURLParam;
        backend.connections.subscribe(connection => { 
        connection.mockRespond(true);
       });
       service.generateOTP("9876543211");
     }));

     it('should check validate',() => {
        backend.connections.subscribe(connection => { 
        connection.mockRespond(true);
       });
       service.validateOTP("9876543212","9876");
     });

     it('should check update',() => {
        backend.connections.subscribe(connection => { 
        connection.mockRespond(true);
       });
       service.update("9876543211","rajath");
     });


//      it('should check failure generateOTP',() => {
//         backend.connections.subscribe(connection => { 
//         connection.mockRespond(false);
//         service.generateOTP("9876543211");
//        });
//      });

//      it('should check failure validate',() => {
//         backend.connections.subscribe(connection => { 
//         connection.mockRespond(false);
//         service.validateOTP("9876");
//        });
//      });

//      it('should check failure update',() => {
//         backend.connections.subscribe(connection => { 
//         connection.mockRespond(false);
//         service.update("9876543211","rajath");
//        });
//      });
});
