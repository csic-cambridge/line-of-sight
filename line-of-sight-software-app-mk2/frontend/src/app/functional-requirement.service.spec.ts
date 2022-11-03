import { TestBed } from '@angular/core/testing';
import { FunctionalRequirementService } from './functional-requirement.service';
import {HttpClientModule} from '@angular/common/http';

describe('FunctionalRequirementService', () => {
  let service: FunctionalRequirementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
        imports: [HttpClientModule]
    });
    service = TestBed.inject(FunctionalRequirementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
