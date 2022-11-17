import { Injectable } from '@angular/core';
import {BaseMeService} from '../base/base-me-service';
import {Observable, of} from 'rxjs';
import {User} from '../../types/user';
import {UserPermissions} from '../../types/user-permissions';
import {ProjectPermissions} from '../../types/project-permissions';

@Injectable({
  providedIn: 'root'
})
export class OfflineMeService extends BaseMeService {
    private user = {is_super_user: false, email_address: 'Local login'} as User;

    constructor() {
        super();
    }

    getMe(): Observable<User> {
        return of(this.user);
    }

    getUserPermissions(): Observable<UserPermissions> {
        return of({} as UserPermissions);
    }

    getProjectPermissions(projectId: string): Observable<ProjectPermissions> {
        return of({} as ProjectPermissions);
    }
}
