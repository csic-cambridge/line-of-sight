import { Injectable } from '@angular/core';
import {Observable, of} from 'rxjs';
import {Oir, OrganisationalObjective} from '../../types/organisational-objective';
import {BaseOrganisationalObjectiveService} from '../base/base-organisational-objective-service';
import {GuidHelper} from '../../helpers/guid-helper';
import {NgxIndexedDBService} from 'ngx-indexed-db';
import {OrganisationalObjectiveVersion} from '../../types/organisational-objective-version';

@Injectable({
  providedIn: 'root'
})
export class OfflineOrganisationalObjectiveService extends BaseOrganisationalObjectiveService {
    constructor(private dbService: NgxIndexedDBService) {
        super();
        this.reload();
    }

    save(organisationalObjective: OrganisationalObjective): Observable<OrganisationalObjective> {
        if (organisationalObjective.id === null) {
            organisationalObjective.id = GuidHelper.getGuid();

            this.dbService
                .add('organisational_objective', organisationalObjective)
                .subscribe((key) => {});

            this.organisationalObjectives.next([...this.organisationalObjectives.value, ...[organisationalObjective]]);
        } else {
            this.dbService
                .update('organisational_objective', organisationalObjective)
                .subscribe((key) => {});
            const find = this.organisationalObjectives.value.find(x => x.id === organisationalObjective.id);
            if (find){
                Object.assign(find, organisationalObjective);
            }
        }
        const version = {
            id: GuidHelper.getGuid(),
            date_created: new Date(),
            oo_id: organisationalObjective.id,
            name: organisationalObjective.name
        } as OrganisationalObjectiveVersion;
        this.dbService
            .add('oo_version', version)
            .subscribe((key) => {});

        organisationalObjective.oirs.filter(oir => oir.id !== '').map(oir => {
            this.dbService
                .update('oirs', oir)
                .subscribe((childkey) => {});
        });
        organisationalObjective.oirs.filter(oir => oir.id === '').map(oir => {
            oir.id = GuidHelper.getGuid();
            oir.oo_id = organisationalObjective.id;
            this.dbService
                .add('oirs', oir)
                .subscribe((childkey) => {});
        });


        return of(organisationalObjective);
    }

    delete(ooId: string): Observable<any> {
        const removed = this.organisationalObjectives.value.filter(x => x.id !== ooId);
        this.organisationalObjectives.next(removed);
        return of(ooId);
    }

    reload(): void {
        this.dbService.getAll('organisational_objective').subscribe((items) => {
            this.organisationalObjectives.next(items as OrganisationalObjective[]);
        });
        this.dbService.getAll('oo_version').subscribe((items) => {
            this.versions.next(items as OrganisationalObjectiveVersion[]);
        });
        this.dbService.getAll('oirs').subscribe((items) => {
            this.oirs.next(items as Oir[]);
        });
    }
}
