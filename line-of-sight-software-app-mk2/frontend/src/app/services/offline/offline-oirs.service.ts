import { Injectable } from '@angular/core';
import {Observable, of} from 'rxjs';
import {BaseOirsService} from "../base/base-oirs-service";
import {GuidHelper} from "../../helpers/guid-helper";
import {Asset} from "../../types/asset";
import {Airs} from "../../types/airs";
import {ProjectOrganisationalObjective} from "../../types/project-organisational-objective";
import {Oir} from "../../types/organisational-objective";

@Injectable({
  providedIn: 'root'
})
export class OfflineOirsService extends BaseOirsService {

  constructor() {super(); }
    linkOirAir(pid: string, oirId: string, airId: string, unlink: boolean): Observable<any> {
            let pooData = localStorage.getItem('projectOrganisationalObjectiveData');
            let assetData = localStorage.getItem("assetData")

            if (pooData && assetData) {
                let poos = [...JSON.parse(pooData)]
                let assets = [...JSON.parse(assetData)]
                let targetPoo!: ProjectOrganisationalObjective;
                let targetAir!: Airs;
                let targetOir!: Oir;
                let oirIndex!: number
                let asset!: Asset;
                let assetIndex!: number
                let airIndex!: number
                let pooIndex!: number;

                // This function does the following
                // 1. Adds and removes Airs from Oirs attached to a given poo
                // 2. Adds and remove Oirs from Airs attached to a given asset
                console.log(oirId)
                console.log(airId)
                // The first step is to get the target air and associate asset
                assets.forEach((ass: Asset, aI: number) => {
                    ass.airs.forEach((a, airI: number) => {
                        if (a.id === airId) {
                            airIndex = airI;
                            assetIndex = aI;
                            targetAir = a
                            asset = ass;
                        }
                    });
                });
                // Secondly we get the target Oir and it's associated Poo
                poos.forEach((p: ProjectOrganisationalObjective, pI:number) => p.oirs.forEach((o, oirI:number) =>  {
                    if (o.id === oirId) {
                        pooIndex = pI;
                        oirIndex = oirI;
                        targetPoo = p
                        targetOir = o
                    }
                }))


            if (unlink) {
                console.log(targetOir)
                // if linking we first add the air to the oir on the poo
                targetOir.airs.push({...targetAir, oirs: [...[]]})
                // then set the new Oir on the poo / oir
                poos[pooIndex].oirs[oirIndex] = targetOir
                // we then add the oir to the air
                console.log(targetAir)
                if (targetAir.oirs) targetAir.oirs.push({...targetOir, airs: [...[]]})
                if (!targetAir.oirs) targetAir.oirs = [{...targetOir, airs: [...[]]}]
                assets[assetIndex].airs[airIndex] = targetAir
            }

            if (!unlink) {
                // if unlinking
                poos[pooIndex].oirs.forEach((o: Oir) => {
                    if (o.airs) o.airs = o.airs.filter((a: Airs) => a.id !== targetAir.id);
                });
                assets[assetIndex].airs.forEach((a: Airs) => {
                    if (a.oirs) a.oirs = a.oirs.filter((o: Oir) => o.id !== targetOir.id);
                });
            }


            localStorage.setItem('projectOrganisationalObjectiveData',JSON.stringify(poos))
            localStorage.setItem('assetData',JSON.stringify(assets))
        }


        return of({})
        // return of(fr);
    }
}
