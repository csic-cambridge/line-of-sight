import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {BehaviorSubject, Observable} from 'rxjs';
import {DataDictionaryEntry} from '../types/data-dictionary-entry';
import {shareReplay} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AssetDataDictionaryEntryService {

    private serviceUrl;
    private cache$: Observable<Array<DataDictionaryEntry>> | undefined;

    public entries$: BehaviorSubject<DataDictionaryEntry[]> = new BehaviorSubject<DataDictionaryEntry[]>([]);

    constructor( private http: HttpClient) {
        this.serviceUrl = environment.apiBaseUrl + '/api/asset-data-dictionary';
    }

    getAssetDataDictionaryEntries(projectId: string): Observable<Array<DataDictionaryEntry>> {
        if (!this.cache$) {
            this.cache$ = this.http.get<Array<DataDictionaryEntry>>(this.serviceUrl + '/' + projectId).pipe(shareReplay());
        }
        return this.cache$;
    }

    load(projectId: string): void {
        this.http.get<DataDictionaryEntry[]>(this.serviceUrl + '/' + projectId)
            .subscribe(data => {
                this.entries$.next(data);
            });
    }

}
