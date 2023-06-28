import { Injectable } from '@angular/core';
import {BaseIoService} from '../base/base-io-service';
import {BaseProjectService} from '../base/base-project-service';
import {OfflineAssetDictionaryService} from './offline-asset-dictionary.service';
import {OfflineFoDictionaryService} from './offline-fo-dictionary.service';
import {OfflineAirsService} from './offline-airs.service';
import {OfflineFirsService} from './offline-firs.service';
import {FunctionalOutput} from '../../types/functional-output';
import {FunctionalRequirement} from '../../types/functional-requirement';
import {Asset} from '../../types/asset';
import {ProjectOrganisationalObjective} from '../../types/project-organisational-objective';
import {map} from 'rxjs/operators';
import {BaseProjectOrganisationalObjectiveService} from '../base/base-project-organisational-objective-service';
import {BaseFunctionalRequirementService} from '../base/base-functional-requirement-service';
import {BaseFunctionalOutputService} from '../base/base-functional-output-service';
import {BaseAssetService} from '../base/base-asset-service';
import versionJson from '../../../../package.json';

@Injectable({
  providedIn: 'root'
})
export class OfflineIoService extends BaseIoService {

  constructor(private projectService: BaseProjectService,
              private assetDictService: OfflineAssetDictionaryService,
              private offlineAirsService: OfflineAirsService,
              private offlineFirsService: OfflineFirsService,
              private foDictService: OfflineFoDictionaryService,
              private pooService: BaseProjectOrganisationalObjectiveService,
              private frService: BaseFunctionalRequirementService,
              private foService: BaseFunctionalOutputService,
              private assetService: BaseAssetService) { super(); }

    exportProject(id: string | undefined): Promise<any> {

        const promise = new Promise<string>((resolve, reject) => {
            const data = {
                import_version: versionJson.version,
                fos: [] as FunctionalOutput[],
                frs: [] as FunctionalRequirement[],
                assets: [] as Asset[],
                poos: [] as ProjectOrganisationalObjective[]
            };
            this.foService.getFunctionalOutputs('').pipe(map(x => data.fos = x)).subscribe();
            this.frService.getFunctionalRequirements('').pipe(map(x => data.frs = x)).subscribe();
            this.assetService.getAssets('').pipe(map(x => data.assets = x)).subscribe();
            this.pooService.getProjectOrganisationalObjectives('').pipe(map(x => data.poos = x)).subscribe();

            if (data) {
                resolve(JSON.stringify(data));
            } else {
                reject('export failed');
            }
        });

        return promise;
    }

    importProject(projectData: string): Promise<any>  {
        const promise = new Promise<string>((resolve, reject) => {
            const project = JSON.parse(projectData);
            if (project.import_version !== versionJson.version){
                reject(`Import file version ${project.import_version} does not match the current version ${versionJson.version}.`);
                return;
            }
            console.log(project)
            const airs = project.assets.map((ass: Asset) => ass.airs)
            const firs = project.fos.map((fo: FunctionalOutput) => fo.firs)

            console.log(airs)
            console.log(firs)
            localStorage.setItem('functionalOutputData', JSON.stringify(project.fos));
            localStorage.setItem('functionalRequirementData', JSON.stringify(project.frs));
            localStorage.setItem('assetData', JSON.stringify(project.assets));
            localStorage.setItem('projectOrganisationalObjectiveData', JSON.stringify(project.poos));
            localStorage.setItem('airsData', JSON.stringify(airs.flat()))
            localStorage.setItem('firsData', JSON.stringify(firs.flat()))
            this.foService.loadFunctionalOutputs('');
            this.frService.loadFunctionalRequirements('');
            this.assetService.loadAssets('');
            this.pooService.loadProjectOrganisationalObjectives('');
            resolve('Import completed.');
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
            if (this.offlineFirsService.import(data)) {
                resolve();
            } else {
                reject();
            }
        });
        return promise;
    }

    importAirs(data: string, projectId: string): Promise<any>  {
        const promise = new Promise<void>((resolve, reject) => {
            if (this.offlineAirsService.import(data)) {
                resolve();
            } else {
                reject();
            }
        });
        return promise;
    }
}
