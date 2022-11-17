import { Observable } from 'rxjs';
import {UserPermissions} from '../../types/user-permissions';
import {User} from '../../types/user';
import {ProjectPermissions} from '../../types/project-permissions';

export abstract class BaseMeService {

    constructor() {}

    abstract getMe(): Observable<User>;

    abstract getUserPermissions(): Observable<UserPermissions>;

    abstract getProjectPermissions(projectId: string): Observable<ProjectPermissions> ;

}
