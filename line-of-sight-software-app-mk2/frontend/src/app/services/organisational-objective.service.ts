import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {OrganisationalObjective} from '../types/organisational-objective';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';
import {HttpClientModule} from '@angular/common/http';
import {BaseOrganisationalObjectiveService} from './base/base-organisational-objective-service';

@Injectable({
    providedIn: 'root'
})
export class OrganisationalObjectiveService extends BaseOrganisationalObjectiveService {

    private serviceUrl;

    constructor(private http: HttpClient) {
        super();
        this.serviceUrl = environment.apiBaseUrl + '/api/organisational-objectives';
    }

    save(organisationalObjective: OrganisationalObjective): Observable<OrganisationalObjective> {
        return organisationalObjective.id === null
            ? this.http.post<OrganisationalObjective>(this.serviceUrl, organisationalObjective)
            : this.http.put<OrganisationalObjective>(this.serviceUrl + '/' + organisationalObjective.id, organisationalObjective);
    }

    delete(ooId: string): Observable<any> {
        return this.http.delete(this.serviceUrl + '/' + ooId);
    }

    reload(): void {
        this.http.get<Array<OrganisationalObjective>>(this.serviceUrl).subscribe(x => {
            this.organisationalObjectives.next(x);
        });
    }

}
