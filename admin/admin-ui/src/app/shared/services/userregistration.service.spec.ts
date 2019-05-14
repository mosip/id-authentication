import { TestBed, inject } from '@angular/core/testing';

import { UserregistrationService } from './userregistration.service';

describe('UserregistrationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserregistrationService]
    });
  });

  it('should be created', inject([UserregistrationService], (service: UserregistrationService) => {
    expect(service).toBeTruthy();
  }));
});
