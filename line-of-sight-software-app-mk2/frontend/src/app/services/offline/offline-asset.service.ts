import { Injectable } from '@angular/core';
import {BaseAssetService} from '../base/base-asset-service';
import {BaseProjectService} from '../base/base-project-service';
import {Observable, of} from 'rxjs';
import {Asset} from '../../types/asset';
import {GuidHelper} from '../../helpers/guid-helper';
import {Airs} from "../../types/airs";

@Injectable({
  providedIn: 'root'
})
export class OfflineAssetService extends BaseAssetService {

    constructor(private ps: BaseProjectService) {
        super(ps);
    }

    getAssets(projectId: string): Observable<Array<Asset>> {
        const assetData = localStorage.getItem('assetData');
        if (assetData) {
            return of(JSON.parse(assetData) as Asset[]);
        }
        return of([] as Asset[]);
    }

    loadAssets(projectId: string): void {
        this.getAssets(projectId).subscribe(x => {
            this.assets.next(x);
        });
    }

    save(asset: Asset, projectId: string): Observable<Asset> {
        if (asset.id === '') {
            asset.id = GuidHelper.getGuid();
            asset.airs[0].id = GuidHelper.getGuid()
            this.assets.next([...this.assets.value, ...[asset]]);
        } else {
            const update = this.assets.value;
            update[update.findIndex(x => x.id === asset.id)] = asset;
            asset.airs.map((air) => {
                if (air.id === "") air.id = GuidHelper.getGuid()
                air.oirs = []
            })
            this.assets.next(update);
        }
        let airs = this.assets.value.map((ass) => ass.airs)
        const airJsonData = JSON.stringify(airs.flat())
        localStorage.setItem('airsData', airJsonData)
        const jsonData = JSON.stringify(this.assets.value);
        localStorage.setItem('assetData', jsonData);
        return of(asset);
    }

    delete(assetId: string, projectId: string): Observable<any> {
        const asset = this.assets.value.find(x => x.id  === assetId);
        this.assets.next(this.assets.value.filter(x => x.id !== assetId));
        const jsonData = JSON.stringify(this.assets.value);
        localStorage.setItem('assetData', jsonData);
        return of(asset);
    }
}
