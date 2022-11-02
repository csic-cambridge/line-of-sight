import { TestBed } from '@angular/core/testing';

import { FunctionalOutputService } from './functional-output.service';

describe('FunctionalOutputService', () => {
  let service: FunctionalOutputService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FunctionalOutputService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
