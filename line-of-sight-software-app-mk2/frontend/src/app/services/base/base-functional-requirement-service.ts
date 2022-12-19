import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {FunctionalRequirement} from '../../types/functional-requirement';
import {BaseProjectService} from './base-project-service';


@Injectable({
    providedIn: 'root'
})
export abstract class BaseFunctionalRequirementService {
    protected projectService: BaseProjectService;
    public functionalRequirements: BehaviorSubject<FunctionalRequirement[]> =
        new BehaviorSubject<FunctionalRequirement[]>([]);

    constructor(ps: BaseProjectService) {
        this.projectService = ps;
    }

    abstract getFunctionalRequirements(projectId: string): Observable<Array<FunctionalRequirement>>;
    abstract loadFunctionalRequirements(projectId: string): void;
    abstract save(functionalRequirement: FunctionalRequirement, projectId: string): Observable<FunctionalRequirement>;
    abstract delete(frId: string, projectId: string): Observable<any>;

}
