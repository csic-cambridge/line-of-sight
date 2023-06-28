import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {ProjectOrganisationalObjective} from "../../types/project-organisational-objective";

@Injectable({
    providedIn: 'root'
})
export abstract class BaseOirsService {
    public projectOrganisationalObjectives: BehaviorSubject<ProjectOrganisationalObjective[]> =
        new BehaviorSubject<ProjectOrganisationalObjective[]>([]);

    constructor() {
    }

    abstract linkOirAir(projectId: string, oirId: string, airId: string, unlink: boolean): Observable<any>;
}
