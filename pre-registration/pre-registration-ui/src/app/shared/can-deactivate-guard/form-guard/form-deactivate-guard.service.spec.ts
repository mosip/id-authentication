import { TestBed } from '@angular/core/testing';

import { FormDeactivateGuardService } from './form-deactivate-guard.service';

describe('FormDeactivateGuardService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FormDeactivateGuardService = TestBed.get(FormDeactivateGuardService);
    expect(service).toBeTruthy();
  });
});
