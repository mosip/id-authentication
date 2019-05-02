import { TestBed, inject } from '@angular/core/testing';

import { AppConfigService } from './app-config.service';
import { of } from 'rxjs';
import { HttpClientModule } from '@angular/common/http';

describe('AppConfigService', () => {

  let service: AppConfigService = null;

  let appConfigService: AppConfigService, mockService = {
    loadAppConfig: jasmine.createSpy('loadAppConfig').and.returnValue(Promise.resolve('hello')),
    getConfig: jasmine.createSpy('getConfig').and.returnValue(of({config: true}))
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [{
        provide: AppConfigService,
        useValue: mockService
      }]
    });
  });

  beforeEach(inject([AppConfigService], (appConfigService) => {
    service = appConfigService
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should load config', () => {
    let response = null;
    service.loadAppConfig().then(value => {
      response = value
      expect(response).toBe('hello')
    });
  });

  it('should get config', () => {
    let response = null;
    service.getConfig().subscribe(value => {
      response = value;
    });
    expect(response).toBeDefined();
    expect(response.config).toBeTruthy();
  })
});
