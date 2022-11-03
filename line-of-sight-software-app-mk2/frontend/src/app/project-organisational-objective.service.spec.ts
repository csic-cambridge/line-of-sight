import {TestBed} from '@angular/core/testing';
import {ProjectOrganisationalObjectiveService} from './project-organisational-objective.service';
import {HttpClientModule} from '@angular/common/http';

describe('ProjectOrganisationalObjectiveService', () => {
    let service: ProjectOrganisationalObjectiveService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientModule]
        });
        service = TestBed.inject(ProjectOrganisationalObjectiveService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
