import { TestBed } from '@angular/core/testing';

import { AssetService } from './asset.service';
import {HttpClientModule} from '@angular/common/http';

describe('AssetService', () => {
  let service: AssetService;

  beforeEach(() => {
    TestBed.configureTestingModule({
        imports: [HttpClientModule]
    });
    service = TestBed.inject(AssetService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
