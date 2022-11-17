import { Injectable } from '@angular/core';
import {Observable, of} from 'rxjs';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import * as pako from 'pako';
import {BaseIoService} from '../base/base-io-service';
import {BaseProjectService} from '../base/base-project-service';
import {Project} from '../../types/project';
import {AssetDictionaryService} from '../asset-dictionary.service';
import {BaseDictionaryService} from '../base/base-dictionary-service';
import {AssetDictionary} from '../../types/asset-dictionary';
import {FunctionalOutputDictionary} from '../../types/functional-output-dictionary';
import {GuidHelper} from '../../helpers/guid-helper';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';
import {OfflineAssetDictionaryService} from './offline-asset-dictionary.service';
import {OfflineFoDictionaryService} from './offline-fo-dictionary.service';

@Injectable({
  providedIn: 'root'
})
export class OfflineIoService extends BaseIoService {

  constructor(private projectService: BaseProjectService,
              private assetDictService: OfflineAssetDictionaryService,
              private foDictService: OfflineFoDictionaryService) { super(); }

    exportProject(id: string | undefined): Promise<any> {

        const promise = new Promise<void>((resolve, reject) => {

            const find = this.projectService.projects.value.find(x => x.id === id);
            if (find) {
                return JSON.stringify(find);
                resolve();
            } else {
                return '';
                reject();
            }
        });

        return promise;
    }

    importProject(projectData: string): Promise<any>  {
        const promise = new Promise<void>((resolve, reject) => {
            this.projectService.save(JSON.parse(projectData) as Project).subscribe({
                next: (res: any) => {
                    return res;
                    resolve();
                },
                error: (err: any) => {
                    reject(err);
                },
            });
        });
        return promise;
    }

    importDictionary(data: string): Promise<any> {
        const promise = new Promise<void>((resolve, reject) => {
            if (this.assetDictService.addDictionary(data)){
                resolve();
            }
            else{
                reject();
            }
        });
        return promise;
    }

    importFODictionary(data: string): Promise<any>   {
        const promise = new Promise<void>((resolve, reject) => {
            if (this.foDictService.addDictionary(data)){ resolve(); }
            else{reject(); }
        });
        return promise;
    }

    importFirs(data: string, projectId: string): Promise<any>   {
        const promise = new Promise<void>((resolve, reject) => {
            if (this.projectService.importFirs(data, projectId)){ resolve(); }
            else{reject(); }
        });
        return promise;
    }

    importAirs(data: string, projectId: string): Promise<any>  {
        const promise = new Promise<void>((resolve, reject) => {
            if (this.projectService.importAirs(data, projectId)){ resolve(); }
            else{reject(); }
        });
        return promise;
    }
}
