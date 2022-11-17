import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {environment} from '../../environments/environment';
import * as pako from 'pako';
import {BaseIoService} from './base/base-io-service';
import {Project} from '../types/project';

@Injectable({
    providedIn: 'root'
})
export class IoService extends BaseIoService {

    constructor(private http: HttpClient) {
        super();
    }

    exportProject(id: string | undefined): Promise<any> {
        return this.http.get(`${environment.apiBaseUrl}/api/project/export/pid/` + id, {
            headers: new HttpHeaders({}),
            responseType: 'text',
            observe: 'response'
        }).toPromise();
    }

    importProject(projectData: string): Promise<any> {
        const output = pako.gzip(projectData);
        const compressed = btoa([...output].map(i => String.fromCharCode(i)).join(''));
        const blob = new Blob([compressed], { type: 'application/octet-stream' });
        return this.http.post(`${environment.apiBaseUrl}/api/project/import`, blob, {
            headers: new HttpHeaders({}),
            responseType: 'text',
            observe: 'response'
        }).toPromise();
    }

    importDictionary(data: string): Promise<any> {
        const output = pako.gzip(data);
        const compressed = btoa([...output].map(i => String.fromCharCode(i)).join(''));
        const blob = new Blob([compressed], { type: 'application/octet-stream' });

        return this.http.post(`${environment.apiBaseUrl}/api/asset-data-dictionary`, blob, {
                headers: new HttpHeaders({}),
                responseType: 'text',
                observe: 'response'
            }).toPromise();
    }

    importFODictionary(data: string): Promise<any>  {
        const output = pako.gzip(data);
        const compressed = btoa([...output].map(i => String.fromCharCode(i)).join(''));
        const blob = new Blob([compressed], { type: 'application/octet-stream' });
        return this.http.post(`${environment.apiBaseUrl}/api/functional-output-data-dictionary`, blob, {
                headers: new HttpHeaders({}),
                responseType: 'text',
                observe: 'response'
            }).toPromise();
    }

    importFirs(data: string, projectId: string): Promise<any>  {
        const output = pako.gzip(data);
        const compressed = btoa([...output].map(i => String.fromCharCode(i)).join(''));
        const blob = new Blob([compressed], { type: 'application/octet-stream' });
        return this.http.post(`${environment.apiBaseUrl}/api/firs/import/pid/${projectId}`, blob, {
            headers: new HttpHeaders({}),
            responseType: 'text',
            observe: 'response'
        }).toPromise();
    }

    importAirs(data: string, projectId: string): Promise<any>  {
        const output = pako.gzip(data);
        const compressed = btoa([...output].map(i => String.fromCharCode(i)).join(''));
        const blob = new Blob([compressed], { type: 'application/octet-stream' });
        return this.http.post(`${environment.apiBaseUrl}/api/airs/import/pid/${projectId}`, blob, {
            headers: new HttpHeaders({}),
            responseType: 'text',
            observe: 'response'
        }).toPromise();
    }
}
