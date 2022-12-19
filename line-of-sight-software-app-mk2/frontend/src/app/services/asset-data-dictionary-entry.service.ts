import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {DataDictionaryEntry} from '../types/data-dictionary-entry';
import {shareReplay} from 'rxjs/operators';
import {BaseAssetDictionaryEntryService} from './base/base-asset-dictionary-entry-service';

@Injectable({
  providedIn: 'root'
})
export class AssetDataDictionaryEntryService extends BaseAssetDictionaryEntryService {

    private serviceUrl;
    private cache$: Observable<Array<DataDictionaryEntry>> | undefined;

    constructor( private http: HttpClient) {
        super();
        this.serviceUrl = environment.apiBaseUrl + '/api/asset-data-dictionary';
    }

    getDataDictionaryEntries(projectId: string): Observable<Array<DataDictionaryEntry>> {
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
