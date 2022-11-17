import { TestBed } from '@angular/core/testing';

import { OfflineOrganisationalObjectiveService } from './offline-organisational-objective.service';

describe('OfflineOrganisationalObjectiveService', () => {
  let service: OfflineOrganisationalObjectiveService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineOrganisationalObjectiveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
