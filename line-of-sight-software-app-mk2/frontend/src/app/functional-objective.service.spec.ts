import { TestBed } from '@angular/core/testing';

import { FunctionalObjectiveService } from './functional-objective.service';

describe('FunctionalObjectiveService', () => {
  let service: FunctionalObjectiveService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FunctionalObjectiveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
