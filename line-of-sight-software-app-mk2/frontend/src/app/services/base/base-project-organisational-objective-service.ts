import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {BaseProjectService} from './base-project-service';
import {ProjectOrganisationalObjective, ProjectOrganisationalObjectiveUpdate} from '../../types/project-organisational-objective';

@Injectable({
    providedIn: 'root'
})
export abstract class BaseProjectOrganisationalObjectiveService {
    public projectOrganisationalObjectives: BehaviorSubject<ProjectOrganisationalObjective[]> =
        new BehaviorSubject<ProjectOrganisationalObjective[]>([]);

    constructor(ps: BaseProjectService) {
    }

    abstract getProjectOrganisationalObjectives(projectId: string): Observable<Array<ProjectOrganisationalObjective>>;

    abstract loadProjectOrganisationalObjectives(projectId: string): void;

    abstract save(poo: ProjectOrganisationalObjective, projectId: string): Observable<ProjectOrganisationalObjectiveUpdate>;

    abstract delete(pooId: string, projectId: string): Observable<any>;


}
