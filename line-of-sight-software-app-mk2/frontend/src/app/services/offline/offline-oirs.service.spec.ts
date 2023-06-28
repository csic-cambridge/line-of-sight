import { TestBed } from '@angular/core/testing';
import {OfflineOirsService} from "./offline-oirs.service";

describe('OfflineOirsService', () => {
  let service: OfflineOirsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineOirsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
