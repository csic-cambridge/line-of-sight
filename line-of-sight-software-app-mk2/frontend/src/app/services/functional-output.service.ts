import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {FunctionalOutput} from '../types/functional-output';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';
import {BaseProjectService} from './base/base-project-service';
@Injectable({
    providedIn: 'root'
})
export class FunctionalOutputService {

    private serviceUrl;
    private projectService: BaseProjectService;

    constructor(private http: HttpClient, private ps: BaseProjectService) {
        this.serviceUrl = environment.apiBaseUrl + '/api/functional-outputs';
        this.projectService = ps;
    }

    getFunctionalOutputs(projectId: string): Observable<Array<FunctionalOutput>> {
        return this.http.get<Array<FunctionalOutput>>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId));
    }

    save(functionalOutput: FunctionalOutput, projectId: string): Observable<FunctionalOutput> {
        console.log('FO about to be pushed : ', functionalOutput);
        return functionalOutput.id === ''
            ? this.http.post<FunctionalOutput>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId), functionalOutput)
            : this.http.put<FunctionalOutput>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId) + '/' + functionalOutput.id, functionalOutput);
    }

    delete(foId: string, projectId: string): Observable<any> {
        console.log('Functional Output Id to delete: ', foId);
        return this.http.delete(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId) + '/' + foId);
    }
}

