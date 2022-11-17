import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {Asset} from '../types/asset';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';
import {BaseProjectService} from './base/base-project-service';
@Injectable({
    providedIn: 'root'
})
export class AssetService {

    private serviceUrl;
    private projectService: BaseProjectService;

    constructor(
        private http: HttpClient,
        private ps: BaseProjectService
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/assets';
        this.projectService = ps;
    }

    getAssets(projectId: string): Observable<Array<Asset>> {
        return this.http.get<Array<Asset>>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId));
    }

    save(asset: Asset, projectId: string): Observable<Asset> {
        return asset.id === ''
            ? this.http.post<Asset>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId), asset)
            : this.http.put<Asset>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId) + '/' + asset.id, asset);
    }

    delete(assetId: string, projectId: string): Observable<any> {
        return this.http.delete(this.serviceUrl + '/' + this.ps.getProjectIdUrlPath(projectId) + '/' + assetId);
    }
}
