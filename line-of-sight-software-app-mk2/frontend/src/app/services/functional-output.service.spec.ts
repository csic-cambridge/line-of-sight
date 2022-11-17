import { TestBed } from '@angular/core/testing';
import { FunctionalOutputService } from './functional-output.service';
import {RouterModule} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';

describe('FunctionalOutputService', () => {
    let service: FunctionalOutputService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [RouterModule, HttpClientModule],
            providers: [FunctionalOutputService]
        });
        service = TestBed.inject(FunctionalOutputService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
