import { Injectable } from '@angular/core';
import {BasePermissionService} from '../base/base-permission-service';
import {UserPermissions} from '../../types/user-permissions';
import {OfflineAssetDictionaryService} from './offline-asset-dictionary.service';
import {OfflineFoDictionaryService} from './offline-fo-dictionary.service';
import {BaseProjectService} from '../base/base-project-service';

@Injectable({
  providedIn: 'root'
})
export class OfflinePermissionService extends BasePermissionService {

    constructor(private assetDictService: OfflineAssetDictionaryService,
                private foDictService: OfflineFoDictionaryService,
                private projectService: BaseProjectService) {
        super();
        this.Reload();
    }

    accountChange(data: UserPermissions): void {
    }

    permissionDisabled(id: string, permissionId: string): boolean {
        return false;
    }

    userPermissionDisabled(permissionId: string): boolean {
        return false;
    }

    Reload(): void {
        this.assetDictService.getDictionaries().subscribe(dict =>
            this.assetDataDictionary.next(dict));
        this.foDictService.getDictionaries().subscribe(dict =>
            this.foDataDictionary.next(dict));
        this.projectService.getProjects().subscribe(proj =>
            this.projects.next(proj));
    }
}
