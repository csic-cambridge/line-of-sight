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

    getAssets(): Observable<Array<Asset>> {
        return this.http.get<Array<Asset>>(this.serviceUrl);
    }

    save(asset: Asset): Observable<Asset> {
        console.log("Asset Id to store: ", asset.id);
        return this.http.put<Asset>(this.serviceUrl + '/' + asset.id, asset);
    }
}
