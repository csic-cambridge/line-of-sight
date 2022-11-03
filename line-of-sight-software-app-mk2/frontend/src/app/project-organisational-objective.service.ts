import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {ProjectOrganisationalObjective} from "./project-organisational-objective";
import {ProjectOrganisationalObjectiveUpdate} from "./project-organisational-objective";
import {HttpClient} from "@angular/common/http";
import {environment} from '../environments/environment';
import { ProjectService } from './project.service';

@Injectable({
    providedIn: 'root'
})
export class ProjectOrganisationalObjectiveService {

    private serviceUrl;
    private projectService: ProjectService;

    constructor(
        private http: HttpClient,
        private ps: ProjectService,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/project-organisational-objectives';
        this.projectService = ps;
    }

    getProjectOrganisationalObjectives(projectId: string): Observable<Array<ProjectOrganisationalObjective>> {

        return this.http.get<Array<ProjectOrganisationalObjective>>(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId));
    }

    save(poo: ProjectOrganisationalObjective, projectId: string): Observable<ProjectOrganisationalObjectiveUpdate> {
        let oir_ids:string[]= [];
        poo.oirs.forEach((oir) => oir_ids.push(oir.id));
        console.log("oirs = ", poo.oirs, oir_ids);
        let pooUpdate = {
            id: poo.id,
            oo_version_id: poo.oo_version_id,
            oo_is_deleted: poo.oo_is_deleted,
            oo_versions: poo.oo_versions,
            oir_ids: oir_ids.flat(),
            frs: poo.frs
        }
        return this.http.put<ProjectOrganisationalObjectiveUpdate>
                    (this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId) + '/' + poo.id, pooUpdate);
    }

    delete(pooId: string, projectId: string): Observable<any> {
        console.log("Project Organisational Objective Id to delete: ", pooId);
        return this.http.delete(this.serviceUrl + '/' + this.projectService.getProjectIdUrlPath(projectId) + '/' + pooId);
    }

}
