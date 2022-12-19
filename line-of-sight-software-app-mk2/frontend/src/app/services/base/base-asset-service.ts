import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {BaseProjectService} from './base-project-service';
import {Asset} from '../../types/asset';
@Injectable({
    providedIn: 'root'
})
export abstract class BaseAssetService {
    protected projectService: BaseProjectService;
    public assets: BehaviorSubject<Asset[]> =
        new BehaviorSubject<Asset[]>([]);

    constructor(projectService: BaseProjectService) {
        this.projectService = projectService;
    }

    abstract getAssets(projectId: string): Observable<Array<Asset>>;
    abstract loadAssets(projectId: string): void;
    abstract save(asset: Asset, projectId: string): Observable<Asset>;
    abstract delete(assetId: string, projectId: string): Observable<any>;
}
