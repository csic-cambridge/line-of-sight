import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {FunctionalObjective} from "./functional-objective";
import {HttpClient} from "@angular/common/http";
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class FunctionalObjectiveService {

    private serviceUrl;

    constructor(private http: HttpClient) {
        this.serviceUrl = environment.apiBaseUrl + '/api/functional-objectives';
    }

    getFunctionalObjectives(): Observable<Array<FunctionalObjective>> {
        return this.http.get<Array<FunctionalObjective>>(this.serviceUrl);
    }

    save(functionalObjective: FunctionalObjective): Observable<FunctionalObjective> {
        console.log("FO about to be pushed : ", functionalObjective);
        return this.http.put<FunctionalObjective>(this.serviceUrl + '/' + functionalObjective.id, functionalObjective);
    }

}
