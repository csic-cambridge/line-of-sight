import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../types/user';
import { UserPermissions } from '../types/user-permissions';
import { ProjectPermissions } from '../types/project-permissions';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import {BaseMeService} from './base/base-me-service';
import {BaseProjectService} from './base/base-project-service';

@Injectable({
    providedIn: 'root'
})

export class MeService extends BaseMeService {

    constructor(
        private http: HttpClient,
        private ps: BaseProjectService
    ) {
        super();
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
