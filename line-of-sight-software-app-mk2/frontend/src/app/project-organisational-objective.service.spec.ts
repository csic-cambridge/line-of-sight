import {TestBed} from '@angular/core/testing';

import {ProjectOrganisationalObjectiveService} from './project-organisational-objective.service';

describe('ProjectOrganisationalObjectiveService', () => {
    let service: ProjectOrganisationalObjectiveService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(ProjectOrganisationalObjectiveService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
