import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { ProjectPermissions } from "./project-permissions";
import { HttpClient } from "@angular/common/http";
import { environment } from '../environments/environment';
import { ProjectService } from './project.service';
@Injectable({
    providedIn: 'root'
})

export class ProjectPermissionService {
    private serviceUrl;

    constructor(
        private http: HttpClient,
        private ps : ProjectService
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/project-permissions';
    }

    getPermissions(userId: string, projectId: string): Observable<ProjectPermissions> {
        return this.http.get<ProjectPermissions>(this.serviceUrl + '/' + userId + '/' + this.ps.getProjectIdUrlPath(projectId));
    }

    save(projectPermissions: ProjectPermissions): Observable<ProjectPermissions> {
        console.log("Storing permissions for user: " + projectPermissions.user_id + " and project: " + projectPermissions.project_id);
        return this.http.put<ProjectPermissions>(this.serviceUrl + '/' + projectPermissions.user_id + '/' +
                    this.ps.getProjectIdUrlPath(projectPermissions.project_id), projectPermissions);
    }

}