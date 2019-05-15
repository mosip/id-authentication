import { TestBed } from '@angular/core/testing';

import { LogPublishersService } from './log.publishers.service';

describe('LogPublishersService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: LogPublishersService = TestBed.get(LogPublishersService);
    expect(service).toBeTruthy();
  });
});
