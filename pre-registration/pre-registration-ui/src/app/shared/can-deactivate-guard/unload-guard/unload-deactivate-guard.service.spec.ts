import { TestBed } from '@angular/core/testing';

import { UnloadDeactivateGuardService } from './unload-deactivate-guard.service';

describe('UnloadDeactivateGuardService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UnloadDeactivateGuardService = TestBed.get(UnloadDeactivateGuardService);
    expect(service).toBeTruthy();
  });
});
