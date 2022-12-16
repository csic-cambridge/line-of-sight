import { Injectable } from '@angular/core';
import {Observable, of} from 'rxjs';
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
        if (this.User.value.user_id === undefined) {
            const userPromise = this.http.get<User>(`${environment.apiBaseUrl}/api/me`).toPromise();
            userPromise.then(user => {
                this.User.next(user);
                return of(user);
            });
        }
        return of(this.User.value);
    }

    getUserPermissions(): Observable<UserPermissions> {
        return this.http.get<UserPermissions>(`${environment.apiBaseUrl}/api/user-permissions/me`);
    }

    getProjectPermissions(projectId: string): Observable<ProjectPermissions> {
        if (this.ProjectPermissions.value.find(x => x.project_id.toString() === projectId) === undefined) {
            const promise = this.http.get<ProjectPermissions>(`${environment.apiBaseUrl}/api/project-permissions/me/${this.ps.getProjectIdUrlPath(projectId)}`).toPromise();
            promise.then(projectPermissions => {
                if (this.ProjectPermissions.value.find(x => x.project_id.toString()  === projectId) === undefined) {
                    const added = [...this.ProjectPermissions.value, ...[projectPermissions]];
                    this.ProjectPermissions.next(added);
                }
            });
        }

        const permissions = this.ProjectPermissions.value.find(x => x.project_id.toString() === projectId);
        if (permissions) {
            return of(permissions);
        }
        else {
            return of({} as ProjectPermissions);
        }
    }

}
