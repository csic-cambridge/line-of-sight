import { Injectable } from '@angular/core';
import {BaseDictionaryService} from '../base/base-dictionary-service';
import {HttpClient} from '@angular/common/http';
import {FunctionalOutputDictionary} from '../../types/functional-output-dictionary';
import {GuidHelper} from '../../helpers/guid-helper';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';
import {Observable} from 'rxjs';
import {NgxIndexedDBService} from 'ngx-indexed-db';

@Injectable({
  providedIn: 'root'
})
export class OfflineFoDictionaryService  extends BaseDictionaryService<FunctionalOutputDictionary>{
    constructor(http: HttpClient, private dbService: NgxIndexedDBService) {
        super(http, 'functional-output-data-dictionary');
    }
    addDictionary(data: string): boolean {
        const all = data.split('\r\n');
        const name = all.shift();
        const dict = {name, id: GuidHelper.getGuid()} as FunctionalOutputDictionary;
        if (all && all.length > 0) {

            this.dbService
                .add('functional_output_dictionary', dict)
                .subscribe((key) => {});
            const dictItems = all.map(x => {
                const split = x.split(',');
                const dictItem = {entry_id: split[0], text: split[1],
                    fo_dictionary_id: dict.id, id: GuidHelper.getGuid()} as DataDictionaryEntry;
                this.dbService
                    .add('functional_output_dictionary_entry', dictItem)
                    .subscribe((childkey) => {});
                return dictItem;
            });

            this.dictionaries.next([...this.dictionaries.value, ...[dict]]);
            this.dictionaryItems.next([...this.dictionaryItems.value, ...dictItems]);
            return true;
        }
        else{
            return false;
        }
    }

    getDictionaries(): Observable<Array<FunctionalOutputDictionary>> {
        this.dbService.getAll('functional_output_dictionary').subscribe((items) => {
            this.dictionaries.next(items as FunctionalOutputDictionary[]);
        });
        this.dbService.getAll('functional_output_dictionary_entry').subscribe((items) => {
            this.dictionaryItems.next(items as DataDictionaryEntry[]);
        });
        return this.dictionaries;
    }
}
