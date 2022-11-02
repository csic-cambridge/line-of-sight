import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {FunctionalOutput} from "./functional-output";
import {HttpClient} from "@angular/common/http";
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class FunctionalOutputService {

    private serviceUrl;

    constructor(private http: HttpClient) {
        this.serviceUrl = environment.apiBaseUrl + '/api/functional-outputs';
    }

    getFunctionalOutputs(projectId: string): Observable<Array<FunctionalOutput>> {
        return this.http.get<Array<FunctionalOutput>>(this.serviceUrl + '/' + projectId);
    }

    save(functionalOutput: FunctionalOutput, projectId: string): Observable<FunctionalOutput> {
        console.log("FO about to be pushed : ", functionalOutput);
        return functionalOutput.id === null
            ? this.http.post<FunctionalOutput>(this.serviceUrl + '/' + projectId, functionalOutput)
            : this.http.put<FunctionalOutput>(this.serviceUrl + '/' + projectId + '/' + functionalOutput.id, functionalOutput);
    }

    delete(foId: string, projectId: string): Observable<any> {
        console.log("Functional Output Id to delete: ", foId);
        return this.http.delete(this.serviceUrl + '/' + projectId + '/' + foId);
    }
}

