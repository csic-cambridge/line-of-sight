import { Injectable } from '@angular/core';
import {BaseDictionaryService} from '../base/base-dictionary-service';
import {AssetDictionary} from '../../types/asset-dictionary';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';
import {GuidHelper} from '../../helpers/guid-helper';
import {Observable} from 'rxjs';
import {NgxIndexedDBService} from 'ngx-indexed-db';

@Injectable({
  providedIn: 'root'
})
export class OfflineAssetDictionaryService extends BaseDictionaryService<AssetDictionary>{
    constructor(private dbService: NgxIndexedDBService) {
        super();
    }

    addDictionary(data: string): boolean {
        const all = data.split('\r\n');
        const name = all.shift();
        const dict = {name, id: GuidHelper.getGuid()} as AssetDictionary;
        if (all && all.length > 0) {

            this.dbService
                .add('asset_dictionary', dict)
                .subscribe((key) => {});
            const dictItems = all.map(x => {
                const split = x.split(',');
                const dictItem = {entry_id: split[0], text: split[1],
                    asset_dictionary_id: dict.id, id: GuidHelper.getGuid()} as DataDictionaryEntry;
                this.dbService
                    .add('asset_dictionary_entry', dictItem)
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

    getDictionaries(): Observable<Array<AssetDictionary>> {
        this.dbService.getAll('asset_dictionary').subscribe((items) => {
            this.dictionaries.next(items as AssetDictionary[]);
        });
        this.dbService.getAll('asset_dictionary_entry').subscribe((items) => {
            this.dictionaryItems.next(items as DataDictionaryEntry[]);
        });
        return this.dictionaries;
    }
}
