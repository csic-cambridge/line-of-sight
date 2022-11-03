import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from './user';
import { UserPermissions } from './user-permissions';
import { ProjectPermissions } from './project-permissions';
import { ProjectService } from './project.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})

export class MeService {

    constructor(
        private http: HttpClient,
        private ps: ProjectService
    ) {
    }

    getMe(): Observable<User> {
        return this.http.get<User>(`${environment.apiBaseUrl}/api/me`);
    }

    getUserPermissions(): Observable<UserPermissions> {
        return this.http.get<UserPermissions>(`${environment.apiBaseUrl}/api/user-permissions/me`);
    }

    getProjectPermissions(projectId: string): Observable<ProjectPermissions> {
        return this.http.get<ProjectPermissions>(`${environment.apiBaseUrl}/api/project-permissions/me/${this.ps.getProjectIdUrlPath(projectId)}`);
    }

}
