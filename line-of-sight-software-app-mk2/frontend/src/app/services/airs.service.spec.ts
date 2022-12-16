import { TestBed } from '@angular/core/testing';

import { AirsService } from './airs.service';

describe('AirsService', () => {
  let service: AirsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AirsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
