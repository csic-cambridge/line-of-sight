import {BehaviorSubject, Observable} from 'rxjs';
import {UserPermissions} from '../../types/user-permissions';
import {User} from '../../types/user';
import {ProjectPermissions} from '../../types/project-permissions';
import {Project} from '../../types/project';

export abstract class BaseMeService {
    public User: BehaviorSubject<User> = new BehaviorSubject<User>({} as User);
    public ProjectPermissions: BehaviorSubject<ProjectPermissions[]> =
        new BehaviorSubject<ProjectPermissions[]>([] as ProjectPermissions[]);
    public UserPermissions: BehaviorSubject<UserPermissions> = new BehaviorSubject<UserPermissions>({} as UserPermissions);
    constructor() {}

    abstract getMe(): Observable<User>;

    abstract getUserPermissions(): Observable<UserPermissions>;

    abstract getProjectPermissions(projectId: string): Observable<ProjectPermissions> ;

}
