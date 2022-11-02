import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {Asset} from "./asset";
import {HttpClient} from "@angular/common/http";
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class AssetService {

    private serviceUrl;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/assets';
    }

    getAssets(projectId: string): Observable<Array<Asset>> {
        return this.http.get<Array<Asset>>(this.serviceUrl + '/' + projectId);
    }

    save(asset: Asset, projectId: string): Observable<Asset> {
        console.log("Asset Id to store: ", asset.id);
        return asset.id === ''
            ? this.http.post<Asset>(this.serviceUrl + '/' + projectId, asset)
            : this.http.put<Asset>(this.serviceUrl + '/' + projectId + '/' + asset.id, asset);
    }

    delete(assetId: string, projectId: string): Observable<any> {
        console.log("Asset Id to delete: ", assetId);
        return this.http.delete(this.serviceUrl + '/' + projectId + '/' + assetId);
    }
}
