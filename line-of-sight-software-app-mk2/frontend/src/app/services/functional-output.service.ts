import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {FunctionalOutput} from '../types/functional-output';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';
import {BaseProjectService} from './base/base-project-service';
import {FunctionalRequirement} from '../types/functional-requirement';
@Injectable({
    providedIn: 'root'
})
export class FunctionalOutputService {

    private serviceUrl;
    private projectService: BaseProjectService;
    public functionalOutputs: BehaviorSubject<FunctionalOutput[]> =
        new BehaviorSubject<FunctionalOutput[]>([]);

    constructor(private http: HttpClient, private ps: BaseProjectService) {
        this.serviceUrl = environment.apiBaseUrl + '/api/functional-outputs';
        this.projectService = ps;
    }

    getFunctionalOutputs(projectId: string): Observable<Array<FunctionalOutput>> {
        return this.http.get<Array<FunctionalOutput>>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId));
    }
    loadFunctionalOutputs(projectId: string): void {
        this.getFunctionalOutputs(projectId).subscribe(x => {
           this.functionalOutputs.next(x);
        });
    }

    save(functionalOutput: FunctionalOutput, projectId: string): Observable<FunctionalOutput> {
        return functionalOutput.id === ''
            ? this.http.post<FunctionalOutput>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId), functionalOutput)
            : this.http.put<FunctionalOutput>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId) + '/' + functionalOutput.id, functionalOutput);
    }

    delete(foId: string, projectId: string): Observable<any> {
        return this.http.delete(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId) + '/' + foId);
    }
}

