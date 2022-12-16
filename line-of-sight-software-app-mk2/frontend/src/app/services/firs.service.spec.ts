import { TestBed } from '@angular/core/testing';

import { FirsService } from './firs.service';

describe('FirsService', () => {
  let service: FirsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FirsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
