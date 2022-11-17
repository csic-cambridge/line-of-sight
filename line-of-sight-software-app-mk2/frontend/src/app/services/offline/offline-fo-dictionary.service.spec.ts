import { TestBed } from '@angular/core/testing';

import { OfflineFoDictionaryService } from './offline-fo-dictionary.service';

describe('OfflineFoDictionaryService', () => {
  let service: OfflineFoDictionaryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineFoDictionaryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
