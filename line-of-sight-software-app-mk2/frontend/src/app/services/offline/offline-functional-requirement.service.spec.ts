import { TestBed } from '@angular/core/testing';

import { OfflineFunctionalRequirementService } from './offline-functional-requirement.service';

describe('OfflineFunctionalRequirementService', () => {
  let service: OfflineFunctionalRequirementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineFunctionalRequirementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
