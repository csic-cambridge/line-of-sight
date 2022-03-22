import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {FunctionalRequirement} from "./functional-requirement";
import {HttpClient} from "@angular/common/http";
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class FunctionalRequirementService {

    private serviceUrl;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/functional-requirements';
    }

    getFunctionalRequirements(): Observable<Array<FunctionalRequirement>> {
        return this.http.get<Array<FunctionalRequirement>>(this.serviceUrl);
    }

    save(functionalRequirement: FunctionalRequirement): Observable<FunctionalRequirement> {
        return this.http.put<FunctionalRequirement>(this.serviceUrl + '/' + functionalRequirement.id, functionalRequirement);
    }

}
