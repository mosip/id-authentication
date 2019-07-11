import { TestBed, inject } from '@angular/core/testing';

import { UinstatusService } from './uinstatus.service';

describe('UinstatusService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UinstatusService]
    });
  });

  it('should be created', inject([UinstatusService], (service: UinstatusService) => {
    expect(service).toBeTruthy();
  }));
});
