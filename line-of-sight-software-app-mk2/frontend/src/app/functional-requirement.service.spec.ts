import { TestBed } from '@angular/core/testing';

import { FunctionalRequirementService } from './functional-requirement.service';

describe('FunctionalRequirementService', () => {
  let service: FunctionalRequirementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FunctionalRequirementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
