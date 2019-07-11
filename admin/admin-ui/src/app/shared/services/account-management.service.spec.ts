import { TestBed, inject } from '@angular/core/testing';

import { AccountManagementService } from './account-management.service';

describe('AccountManagementService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AccountManagementService]
    });
  });

  it('should be created', inject([AccountManagementService], (service: AccountManagementService) => {
    expect(service).toBeTruthy();
  }));
});
