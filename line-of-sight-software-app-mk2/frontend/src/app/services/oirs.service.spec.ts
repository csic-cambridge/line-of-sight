import { TestBed } from '@angular/core/testing';

import { AirsService } from './airs.service';
import {OirsService} from "./oirs.service";

describe('OirsService', () => {
  let service: OirsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OirsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
