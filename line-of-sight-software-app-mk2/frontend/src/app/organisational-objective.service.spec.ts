import { TestBed } from '@angular/core/testing';

import { OrganisationalObjectiveService } from './organisational-objective.service';

describe('OrganisationalObjectiveService', () => {
  let service: OrganisationalObjectiveService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrganisationalObjectiveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
