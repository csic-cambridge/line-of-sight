import { TestBed } from '@angular/core/testing';

import { OfflineAirsService } from './offline-airs.service';

describe('OfflineAirsService', () => {
  let service: OfflineAirsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineAirsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
