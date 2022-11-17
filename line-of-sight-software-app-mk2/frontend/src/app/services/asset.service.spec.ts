import { TestBed } from '@angular/core/testing';

import { AssetService } from './asset.service';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('AssetService', () => {
  let service: AssetService;

  beforeEach(() => {
    TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(AssetService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
