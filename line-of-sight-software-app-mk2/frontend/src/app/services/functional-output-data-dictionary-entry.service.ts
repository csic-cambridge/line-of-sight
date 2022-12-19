import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {BehaviorSubject, Observable} from 'rxjs';
import {DataDictionaryEntry} from '../types/data-dictionary-entry';
import {shareReplay} from 'rxjs/operators';
import {BaseFunctionalOutputDictionaryEntryService} from './base/base-functional-output-dictionary-entry-service';

@Injectable({
  providedIn: 'root'
})
export class FunctionalOutputDataDictionaryEntryService extends BaseFunctionalOutputDictionaryEntryService {

    private serviceUrl;
    private cache$: Observable<Array<DataDictionaryEntry>> | undefined;

    constructor(private http: HttpClient) {
        super();
        this.serviceUrl = environment.apiBaseUrl + '/api/functional-output-data-dictionary';
    }

    load(projectId: string): void {
        this.http.get<DataDictionaryEntry[]>(this.serviceUrl + '/' + projectId)
            .subscribe(data => {
                this.entries$.next(data);
            });
    }

    getDataDictionaryEntries(projectId: string): Observable<Array<DataDictionaryEntry>> {
        if (!this.cache$) {
            this.cache$ = this.http.get<Array<DataDictionaryEntry>>(this.serviceUrl + '/' + projectId).pipe(shareReplay());
        }
        return this.cache$;
    }
}
