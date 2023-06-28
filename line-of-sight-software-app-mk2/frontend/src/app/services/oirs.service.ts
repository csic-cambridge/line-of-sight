import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {BaseOirsService} from "./base/base-oirs-service";
@Injectable({
  providedIn: 'root'
})
export class OirsService extends BaseOirsService {

    private serviceUrl: string;
    constructor(
        private http: HttpClient
    ) {
        super();
        this.serviceUrl = environment.apiBaseUrl + '/api/oirs';
    }

    linkOirAir(projectId:string,oirId:string, airId:string,link:boolean): Observable<any> {
        console.log(projectId)
        console.log(oirId)
        console.log(airId)
        console.log(link)

        return this.http.post(`${this.serviceUrl}/link/${link ? 1 : 0}/pid/${projectId}`, {
            oirId: oirId,
            airId: airId
        });
    }
}
