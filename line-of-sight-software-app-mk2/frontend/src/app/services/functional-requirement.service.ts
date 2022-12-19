import { Injectable } from '@angular/core';
import {FunctionalRequirement} from '../types/functional-requirement';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';
import {BaseProjectService} from './base/base-project-service';
import {BaseFunctionalRequirementService} from './base/base-functional-requirement-service';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class FunctionalRequirementService extends BaseFunctionalRequirementService {
    private serviceUrl;

    constructor(private http: HttpClient,
                private ps: BaseProjectService) {
        super(ps);
        this.serviceUrl = environment.apiBaseUrl + '/api/functional-requirements';
    }

    getFunctionalRequirements(projectId: string): Observable<Array<FunctionalRequirement>> {
        return this.http.get<Array<FunctionalRequirement>>(this.serviceUrl + '/' + this.ps.getProjectIdUrlPath(projectId));
    }

    loadFunctionalRequirements(projectId: string): void {
        this.getFunctionalRequirements(projectId).subscribe(x => {
            this.functionalRequirements.next(x);
        });
    }

    save(functionalRequirement: FunctionalRequirement, projectId: string): Observable<FunctionalRequirement> {
        return functionalRequirement.id === ''
            ? this.http.post<FunctionalRequirement>(this.serviceUrl + '/' + this.ps.getProjectIdUrlPath(projectId), functionalRequirement)
            : this.http.put<FunctionalRequirement>(this.serviceUrl + '/' + this.ps.getProjectIdUrlPath(projectId) +
                '/' + functionalRequirement.id, functionalRequirement);

    }

    delete(frId: string, projectId: string): Observable<any> {
        return this.http.delete(this.serviceUrl + '/' + this.ps.getProjectIdUrlPath(projectId) + '/' + frId);
    }
}
