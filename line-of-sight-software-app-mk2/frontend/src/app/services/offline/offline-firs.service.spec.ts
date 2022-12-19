import { TestBed } from '@angular/core/testing';

import { OfflineFirsService } from './offline-firs.service';

describe('OfflineFirsService', () => {
  let service: OfflineFirsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineFirsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
