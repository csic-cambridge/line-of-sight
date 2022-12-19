import { TestBed } from '@angular/core/testing';

import { OfflineProjectOrganisationalObjectiveService } from './offline-project-organisational-objective.service';

describe('OfflineProjectOrganisationalObjectiveService', () => {
  let service: OfflineProjectOrganisationalObjectiveService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineProjectOrganisationalObjectiveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
