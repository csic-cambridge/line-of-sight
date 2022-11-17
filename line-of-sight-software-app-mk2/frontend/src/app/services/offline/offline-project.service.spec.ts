import { TestBed } from '@angular/core/testing';

import { OfflineProjectService } from './offline-project.service';

describe('OfflineProjectService', () => {
  let service: OfflineProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineProjectService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
