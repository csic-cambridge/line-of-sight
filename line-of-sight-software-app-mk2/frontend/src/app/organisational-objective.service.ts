import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {OrganisationalObjective} from "./organisational-objective";
import {HttpClient} from "@angular/common/http";
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class OrganisationalObjectiveService {

    private serviceUrl;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/organisational-objectives';
    }

    getOrganisationalObjectives(): Observable<Array<OrganisationalObjective>> {
        return this.http.get<Array<OrganisationalObjective>>(this.serviceUrl);
    }

    save(organisationalObjective: OrganisationalObjective): Observable<OrganisationalObjective> {
        return organisationalObjective.id === null
            ? this.http.post<OrganisationalObjective>(this.serviceUrl, organisationalObjective)
            : this.http.put<OrganisationalObjective>(this.serviceUrl + '/' + organisationalObjective.id, organisationalObjective);
    }

    delete(ooId: string): Observable<any> {
        console.log("Organisational Objective Id to delete: ", ooId);
        return this.http.delete(this.serviceUrl + '/' + ooId);
    }

}
